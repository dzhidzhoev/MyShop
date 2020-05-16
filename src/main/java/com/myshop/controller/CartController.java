package com.myshop.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.myshop.ShopUserPrincipal;
import com.myshop.model.Cart.ID;
import com.myshop.repository.CartRepository;

@Controller
public class CartController {
	@Autowired UserController userController;
	@Autowired CartRepository cartRepo;
	
	@GetMapping("/user/cart")
	public String showCart(Model model) {
		model.addAttribute("cart", cartRepo.findByUserId(userController.getLoggedUser().getId()));
		return "cart";
	}
	
	@PostMapping("/user/add_to_cart")
	public String addToCart(int itemId, int count, Optional<String> redir) {
		Integer userId = userController.getLoggedUserId();
		if (userId == null) {
			return "redirect:/login";
		}
		var cartItem = cartRepo.findById(new ID(userId, itemId));
		if (cartItem.isPresent()) {
			cartItem.get().setCount(cartItem.get().getCount() + count);
			cartRepo.saveAndFlush(cartItem.get());
		} else {
			cartRepo.addToCart(userController.getLoggedUserId(), itemId, count);
		}
		if (redir.isPresent()) {
			return "redirect:" + redir.get();
		}
		return "redirect:/item?id=" + itemId + "&added=1";
	}
	
	@PostMapping("/user/add_count_item_cart") 
	public String changeItemCount(int userId, int itemId, int count) {
		var loggedUser = userController.getLoggedUser();
		if (loggedUser == null || !(loggedUser.getId() == userId || loggedUser.isAdmin())) {
			return "redirect:/user/cart";
		}
		
		var cartItem = cartRepo.findById(new ID(userId, itemId)).get();
		if (cartItem.getCount() + count > 0) {
			cartRepo.saveAndFlush(cartItem.setCount(cartItem.getCount() + count));
		}
		return "redirect:/user/cart";
	}
	
	@PostMapping("/user/delete_item_cart")
	public String deleteFromCart(int itemId) {
		cartRepo.deleteById(new ID(userController.getLoggedUserId(), itemId));
		return "redirect:/user/cart";
	}
}
