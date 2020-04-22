package com.myshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartController {
	@Autowired UserController userController;
	
	@GetMapping("/user/cart")
	public String showCart() {
		return "cart";
	}
}
