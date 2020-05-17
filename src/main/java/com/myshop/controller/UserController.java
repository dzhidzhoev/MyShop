package com.myshop.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.myshop.ShopAuthProvider;
import com.myshop.ShopUserPrincipal;
import com.myshop.model.User;
import com.myshop.repository.OrderRepository;
import com.myshop.repository.UserRepository;

@Controller
public class UserController {
	public static final String ADMIN_USERS_PATH = "/admin/users";
	
	private static final String SAME_PWD = "samePwd";
	private static final String ERROR_MESSAGE = "errorMessage";
	
	@Autowired Environment env;
	@Autowired UserRepository userRepo;
	@Autowired OrderRepository orderRepo;
	@Autowired ShopAuthProvider authProvider;
	@Autowired JavaMailSender mailSender;
	
	public User getLoggedUser() {
		var security = SecurityContextHolder.getContext();
		if (security.getAuthentication() == null 
				|| !(security.getAuthentication().getPrincipal() instanceof ShopUserPrincipal)) {
			return null;
		}
		ShopUserPrincipal principal = (ShopUserPrincipal) security.getAuthentication().getPrincipal();
		return principal.getUser();
	}
	
	public Integer getLoggedUserId() {
		var user = getLoggedUser();
		return user != null ? user.getId() : null;
	}
	
	@RequestMapping(value="/login",method=RequestMethod.GET)
	public String showLogin() {
		return "login";
	}
	
	@RequestMapping(value="/register", method = RequestMethod.GET)
	public String showRegister(Model model,
			@RequestParam(name = SAME_PWD, required = false) String samePwd,
			@RequestParam(name = ERROR_MESSAGE, required = false) String errorMessage) {
		if (samePwd != null) {
			model.addAttribute(SAME_PWD, samePwd);
		}
		if (errorMessage != null) {
			model.addAttribute(ERROR_MESSAGE, errorMessage);
		}
		return "register";
	}
	
	@PostMapping("/register")
	public String doRegister(Model model, HttpServletRequest request, @RequestParam(name = "lastName") String lastName, 
			@RequestParam(name = "firstName") String firstName, 
			@RequestParam(name = "middleName", required = false) String middleName, 
			@RequestParam(name = "phoneNumber", required = false) String phoneNumber, 
			@RequestParam(name = "address", required = false) String address, 
			@RequestParam(name = "email") String email, 
			@RequestParam(name = "password") String password,
			@RequestParam(name = "password2") String password2) throws UnsupportedEncodingException {
		return registerOrUpdate(model, request, "register", null, lastName, firstName, middleName, phoneNumber, address, email, password,
				password2);
	}
	
	@PostMapping("/update_user_info")
	public String doUpdate(Model model, HttpServletRequest request,
			@RequestParam(name = "userId") Integer id,
			@RequestParam(name = "lastName") String lastName, 
			@RequestParam(name = "firstName") String firstName, 
			@RequestParam(name = "middleName") String middleName, 
			@RequestParam(name = "phoneNumber") String phoneNumber, 
			@RequestParam(name = "address") String address, 
			@RequestParam(name = "email") String email, 
			@RequestParam(name = "password") String password,
			@RequestParam(name = "password2") String password2) throws UnsupportedEncodingException {
		var userId = getLoggedUserId();
		if (userId == null) {
			return "redirect:/login";
		}
		var user = userRepo.findById(userId).get();
		if (Boolean.valueOf(true).equals(user.isAdminOrNull()) || Integer.valueOf(user.getId()).equals(id)) {
			return registerOrUpdate(model, request, "redirect:/profile", id, lastName, firstName, middleName, phoneNumber, address, email, password,
					password2);
		} else {
			return "redirect:/login";
		}
	}
	
	private void fillModelData(Model model, Integer id, String lastName, String firstName, String middleName,
			String phoneNumber, String address, String email, String password, String password2) {
		if (id != null) {
			model.addAttribute("form_userId", id);	
		}
		model.addAttribute("form_lastName", lastName);
		model.addAttribute("form_firstName", firstName);
		model.addAttribute("form_middleName", middleName);
		model.addAttribute("form_phoneNumber", phoneNumber);
		model.addAttribute("form_address", address);
		model.addAttribute("form_email", email);
		model.addAttribute("form_password", password);
		model.addAttribute("form_password1", password2);
	}
	
	private String registerOrUpdate(Model model, HttpServletRequest request, String errorUrl, Integer id, String lastName, String firstName,
			String middleName, String phoneNumber, String address, String email, String password, String password2)
			throws UnsupportedEncodingException {
		Function<String, String> prepareParams = (String msg) -> {
			if (errorUrl.startsWith("redirect:")) {
				return msg;
			} else {
				return "";
			}
		};
		
		if (!password.equals(password2)) {
			model.addAttribute(SAME_PWD, true);
			fillModelData(model, id, lastName, firstName, middleName, phoneNumber, address, email, password, password2);
			return errorUrl + prepareParams.apply("?samePwd=true");
		}
		var attempt = userRepo.registerUser(id, lastName, firstName, middleName, phoneNumber, address, email, password);
		if (attempt.getFirst().isEmpty()) {
			model.addAttribute(ERROR_MESSAGE, attempt.getSecond() + "");
			fillModelData(model, id, lastName, firstName, middleName, phoneNumber, address, email, password, password2);
			return errorUrl + prepareParams.apply("?errorMessage=" + URLEncoder.encode(attempt.getSecond() + "", "UTF-8"));
		}
		
		if (id == null) {
			var user = attempt.getFirst().get();
			SecurityContextHolder.getContext().setAuthentication(
					authProvider.authenticate(new UsernamePasswordAuthenticationToken(email, password)));
			
			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setTo(user.getEmail());
			msg.setSubject("Подтверждение пароля - MyShop");
			msg.setText("Для подтверждения пароля перейдите по ссылке " +
					"http://" + request.getLocalName() + "/confirm?email=" + URLEncoder.encode(user.getEmail(), "UTF-8") +
					"&token=" + URLEncoder.encode(user.getEmailToken(), "UTF-8"));
			msg.setFrom(env.getProperty("spring.mail.username"));
			mailSender.send(msg);
		}
		
		return "redirect:/profile";
	}
	
	@GetMapping("/confirm")
	public String doConfirm(@RequestParam(name = "email") String email, 
			@RequestParam(name = "token") String token) {
		if (userRepo.findByEmailIgnoreCase(email).isPresent()) {
			userRepo.approveEmail(token);
		}
		return "redirect:/login";
	}
	
	@GetMapping("/forgot")
	public String showForgot() {
		return "forgot";
	}
	
	@PostMapping("/forgot")
	public String restorePassword(Model model, HttpServletRequest request, @RequestParam(name = "email") String email) throws UnsupportedEncodingException {
		email = email.trim();
		if (email.isEmpty()) {
			model.addAttribute("isEmailEmpty", "true");
			return "forgot";
		}
		var attempt = userRepo.findByEmailIgnoreCase(email);
		if (attempt.isPresent() && !attempt.get().isDeleted()) {
			var user = attempt.get();
			user.setPwdChangeToken(UUID.randomUUID().toString());
			userRepo.saveAndFlush(user);
			
			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setTo(user.getEmail());
			msg.setSubject("Сброс пароля - MyShop");
			msg.setText("Для сброса своего действующего пароля перейдите по ссылке " +
					"http://" + request.getLocalName() + "/reset?email=" + URLEncoder.encode(user.getEmail(), "UTF-8") +
					"&token=" + URLEncoder.encode(user.getPwdChangeToken(), "UTF-8"));
			msg.setFrom(env.getProperty("spring.mail.username"));
			mailSender.send(msg);
		}
		
		return "redirect:/login?restored=1";
	}
	
	@GetMapping("/reset")
	public String showReset() {
		return "reset";
	}
	
	@PostMapping("/reset")
	public String doReset(Model model, @RequestParam(name = "email") String email,
			@RequestParam(name = "token") String token,
			@RequestParam(name = "password") String password,
			@RequestParam(name = "password2") String password2) {
		if (!password.equals(password2)) {
			model.addAttribute(SAME_PWD, "true");
			return "reset";
		}
		if (!userRepo.isPasswordValid(password)) {
			model.addAttribute(ERROR_MESSAGE, "Пароль содержит запрещённые символы или имеет длину меньше 8 знаков!");
			return "reset";
		}
		var attempt = userRepo.findByEmailIgnoreCase(email);
		if (attempt.isPresent()) {
			userRepo.resetPassword(token, password);
		}
		return "redirect:/login?pwdchanged=1";
	}
	
	@GetMapping("/profile")
	public String showProfile(Model model,
			// error codes
			@RequestParam(name = SAME_PWD, required = false) Boolean samePwd,
			@RequestParam(name = ERROR_MESSAGE, required = false) String errorMessage) {
		Integer userId = getLoggedUserId();
		if (userId == null) {
			return "redirect:/login";
		}
		var user = userRepo.findById(userId).get();
		model.addAttribute("user", user);
		Pageable order = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("orderTime").descending());
		var orders = orderRepo.findByUserId(user.getId(), order);
		model.addAttribute("orders", orders);
		if (samePwd != null) {
			model.addAttribute(SAME_PWD, samePwd);
		}
		if (errorMessage != null) {
			model.addAttribute(ERROR_MESSAGE, errorMessage);
		}
		fillModelData(model, user.getId(), user.getLastName(), user.getFirstName(), user.getMiddleName(), user.getPhone(), user.getAddress(), user.getEmail(), "", "");
		return "profile";
	}
	
	@GetMapping(ADMIN_USERS_PATH)
	public String showUsersManagement(Model model) {
		model.addAttribute("users", userRepo.findAll());
		return "users";
	}
	
	@PostMapping("/admin/delete_user")
	public String deleteUser(int id) {
		userRepo.deleteById(id);
		return "redirect:" + ADMIN_USERS_PATH;
	}
}
