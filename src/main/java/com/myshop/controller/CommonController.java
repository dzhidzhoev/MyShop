package com.myshop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.myshop.ShopUserPrincipal;
import com.myshop.model.Category;
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
}
