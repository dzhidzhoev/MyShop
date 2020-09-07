package com.myshop.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.myshop.model.Order;
import com.myshop.repository.OrderRepository;
import com.myshop.repository.UserRepository;

@Controller
public class OrderController {
	public static final String ADMIN_ORDERS_PATH = "/admin/orders";
	public static final int PAGE_SIZE = 25;
	
	@Autowired OrderRepository orderRepo;
	@Autowired UserRepository userRepo;
	
	@GetMapping(ADMIN_ORDERS_PATH)
	public String showOrdersManagement(Model model, Optional<Integer> page, Optional<String> email) {
		List<Order> orders;
		Optional<Integer> userId = email.flatMap(x -> userRepo.findByEmailIgnoreCase(x)).map(x -> x.getId());
		Integer pageNum = page.orElse(0);
		Pageable pagination = PageRequest.of(pageNum, PAGE_SIZE, Direction.DESC, "OrderTime");
		if (userId.isPresent()) {
			orders = orderRepo.findByUserId(userId.get(), pagination);
		} else {
			orders = orderRepo.findAll(pagination).toList();
		}
		model.addAttribute("page", pageNum + 1);
		model.addAttribute("orders", orders);
		return "orders";
	}
}
