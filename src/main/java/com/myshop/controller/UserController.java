package com.myshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.myshop.ShopAuthProvider;
import com.myshop.repository.UserRepository;

@Controller
public class UserController {
	@Autowired UserRepository userRepo;
	@Autowired ShopAuthProvider authProvider;
	
	@RequestMapping(value="/login",method=RequestMethod.GET)
	public String showLogin() {
		return "login";
	}
	
	@RequestMapping(value="/register", method = RequestMethod.GET)
	public String showRegister() {
		return "register";
	}
	
	@PostMapping("/register")
	public String doRegister(Model model, @RequestParam(name = "lastName") String lastName, 
			@RequestParam(name = "firstName") String firstName, 
			@RequestParam(name = "middleName", required = false) String middleName, 
			@RequestParam(name = "phoneNumber", required = false) String phoneNumber, 
			@RequestParam(name = "address", required = false) String address, 
			@RequestParam(name = "email") String email, 
			@RequestParam(name = "password") String password,
			@RequestParam(name = "password2") String password2) {
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
		SecurityContextHolder.getContext().setAuthentication(
				authProvider.authenticate(new UsernamePasswordAuthenticationToken(email, password)));
		return "redirect:/profile";
	}
}
