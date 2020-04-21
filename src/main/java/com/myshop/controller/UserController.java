package com.myshop.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
import com.myshop.repository.UserRepository;

@Controller
public class UserController {
	@Autowired Environment env;
	@Autowired UserRepository userRepo;
	@Autowired ShopAuthProvider authProvider;
	@Autowired JavaMailSender mailSender;
	
	@RequestMapping(value="/login",method=RequestMethod.GET)
	public String showLogin() {
		return "login";
	}
	
	@RequestMapping(value="/register", method = RequestMethod.GET)
	public String showRegister() {
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
		Runnable keepFormData = new Runnable() {
			
			@Override
			public void run() {
				model.addAttribute("form_lastName", lastName);
				model.addAttribute("form_firstName", firstName);
				model.addAttribute("form_middleName", middleName);
				model.addAttribute("form_phoneNumber", phoneNumber);
				model.addAttribute("form_address", address);
				model.addAttribute("form_email", email);
			}
		};
		
		if (!password.equals(password2)) {
			model.addAttribute("samePwd", true);
			keepFormData.run();
			return "register";
		}
		var attempt = userRepo.registerUser(null, lastName, firstName, middleName, phoneNumber, address, email, password);
		if (attempt.getFirst().isEmpty()) {
			model.addAttribute("errorMessage", attempt.getSecond() + "");
			keepFormData.run();
			return "register";
		}
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
			model.addAttribute("samePwd", "true");
			return "reset";
		}
		if (!userRepo.isPasswordValid(password)) {
			model.addAttribute("errorMessage", "Пароль содержит запрещённые символы или имеет длину меньше 8 знаков!");
			return "reset";
		}
		var attempt = userRepo.findByEmailIgnoreCase(email);
		if (attempt.isPresent()) {
			userRepo.resetPassword(token, password);
		}
		return "redirect:/login?pwdchanged=1";
	}
}
