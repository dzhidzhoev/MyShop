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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.myshop.model.Order;
import com.myshop.model.OrderStatus;
import com.myshop.repository.CartRepository;
import com.myshop.repository.ItemRepository;
import com.myshop.repository.OrderItemRepository;
import com.myshop.repository.OrderRepository;
import com.myshop.repository.UserRepository;

@Controller
public class OrderController {
	public static final String ADMIN_ORDERS_PATH = "/admin/orders";
	public static final int PAGE_SIZE = 25;
	
	@Autowired CommonController common;
	@Autowired OrderRepository orderRepo;
	@Autowired UserRepository userRepo;
	@Autowired CartRepository cartRepo;
	@Autowired ItemRepository itemRepo;
	@Autowired OrderItemRepository orderItemRepo;
	@Autowired UserController userController;
	
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
	
	private boolean hasOrderAccess(Order order) {
		return common.isUserAdmin() || userController.getLoggedUserId() == order.getUser().getId(); 
	}
	
	@PostMapping
	public String placeOrder() {
		var o = orderRepo.placeNewOrder(userController.getLoggedUser(), cartRepo, itemRepo, orderItemRepo);
		if (o.getFirst().isEmpty()) {
			return "redirect:/user/cart";
		}
		return "redirect:/user/order?id=" + o.getFirst().get().getId();
	}
	
	@GetMapping("/user/order")
	public String showOrder(Model model, @RequestParam int id) {
		var orderAttempt = orderRepo.findById(id);
		if (orderAttempt.isEmpty() || !hasOrderAccess(orderAttempt.get())) {
			return "redirect:/profile";
		}
		Order order = orderAttempt.get();
		model.addAttribute("order", order);
		model.addAttribute("items", orderItemRepo.findByOrderId(order.getId()));
		return "order";
	}
	
	@PostMapping("/user/cancel")
	public String cancelOrder(@RequestParam int id) {
		var orderAttempt = orderRepo.findById(id);
		if (!orderAttempt.isEmpty() 
				&& hasOrderAccess(orderAttempt.get())
				&& orderAttempt.get().getStatus() != OrderStatus.Done) {
			var order = orderAttempt.get()
					.setStatus(OrderStatus.Canceled);
			orderRepo.saveAndFlush(order);
		}
		return "redirect:/user/order?id=" + id;
	}
	
	@PostMapping("/admin/set_order_status")
	public String setOrderStatus(@RequestParam int id, @RequestParam OrderStatus status) {
		var orderAttempt = orderRepo.findById(id);
		if (orderAttempt.isEmpty()) {
			return "redirect:" + ADMIN_ORDERS_PATH;
		}
		var order = orderAttempt.get();
		orderRepo.saveAndFlush(order.setStatus(status));
		return "redirect:/user/order?id=" + id;
	}
}
