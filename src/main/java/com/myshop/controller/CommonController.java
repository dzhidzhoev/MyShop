package com.myshop.controller;

import java.security.InvalidParameterException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.myshop.ShopUserPrincipal;
import com.myshop.WebSecurityConfig;
import com.myshop.model.Category;
import com.myshop.model.OrderStatus;
import com.myshop.repository.CategoryRepository;

@ControllerAdvice
public class CommonController {
	@Autowired CategoryRepository catRepo;
	
	@ModelAttribute("menuCategories")
	public List<Category> getMenuCategories() {
		return catRepo.findAll();
	}
	
	@ModelAttribute("isAuthorized")
	public boolean isUserAuthorized() {
		var secContext = SecurityContextHolder.getContext();
		var res = secContext.getAuthentication() != null 
				&& (secContext.getAuthentication().getPrincipal() instanceof ShopUserPrincipal);
		return res;
	}
	
	@ModelAttribute("isUserAdmin")
	public boolean isUserAdmin() {
		var secContext = SecurityContextHolder.getContext();
		var res = isUserAuthorized() && ((ShopUserPrincipal) secContext.getAuthentication().getPrincipal())
				.getAuthorities().stream()
				.anyMatch(auth -> auth.getAuthority().equals(WebSecurityConfig.ADMIN_AUTHORITY));
		return res;
	}

	public static String getOrderStatusString(OrderStatus status) {
		switch (status) {
		case Canceled:
			return "Отменён";
		case Delivery:
			return "Идёт доставка";
		case Done:
			return "Завершён";
		case Processing:
			return "В обработке";
		default:
			throw new InvalidParameterException("unsupported OrderStatus value");
		}
	}
	
	public static String getOrderStatusHTML(OrderStatus status) {
		String pre = "<div class=\"text-danger\">";
		switch (status) {
		case Canceled:
			pre = "<div>";
			break;
		case Delivery:
			pre = "<div class=\"text-info\">";
			break;
		case Done:
			pre = "<div class=\"text-success\">";
			break;
		case Processing:
			pre = "<div class=\"text-warning\">";
			break;
		}
		String post = "</div>";
		return pre + getOrderStatusString(status) + post;
	}
}
