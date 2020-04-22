package com.myshop.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.myshop.model.Category;
import com.myshop.repository.CategoryRepository;

@Controller
public class CategoryController {
	public static final String ADMIN_CATEGORIES_PATH = "/admin/categories";
	@Autowired CategoryRepository catRepo;
	
	@GetMapping(ADMIN_CATEGORIES_PATH)
	public String showCategoryManagement(Model model) {
		model.addAttribute("categories", catRepo.findAll());
		return "categories";
	}
	
	@PostMapping("/admin/toggle_cat_is_active")
	public String toggleCategoryActive(@RequestParam Category cat) {
		cat.setActive(!cat.isActive());
		catRepo.saveAndFlush(cat);
		return "redirect:" + ADMIN_CATEGORIES_PATH;
	}
	
	@PostMapping("/admin/add_cat")
	public String addCategory(@RequestParam String name, @RequestParam boolean active) throws UnsupportedEncodingException {
		if (name.trim().isEmpty()) {
			return "redirect:" + ADMIN_CATEGORIES_PATH + "?errorMessage=noname";
		}
		Category category = new Category(name, active);
		catRepo.saveAndFlush(category);
		return "redirect:" + ADMIN_CATEGORIES_PATH;
	}
}
