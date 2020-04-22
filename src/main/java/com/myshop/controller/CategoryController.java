package com.myshop.controller;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.myshop.model.Category;
import com.myshop.model.CategoryTrait;
import com.myshop.repository.CategoryRepository;
import com.myshop.repository.CategoryTraitRepository;
import com.myshop.repository.TraitRepository;

@Controller
public class CategoryController {
	public static final String ADMIN_CATEGORY_PATH = "/admin/category";
	public static final String ADMIN_CATEGORIES_PATH = "/admin/categories";
	@Autowired CategoryRepository catRepo;
	@Autowired CategoryTraitRepository catTraitRepo;
	@Autowired TraitRepository traitRepo;
	
	@GetMapping(ADMIN_CATEGORIES_PATH)
	public String showCategoriesManagement(Model model) {
		model.addAttribute("categories", catRepo.findAll());
		return "categories";
	}
	
	@GetMapping(ADMIN_CATEGORY_PATH) 
	public String showCategoryManagement(Model model, @RequestParam int categoryId) {
		model.addAttribute("category", catRepo.findById(categoryId).get());
		var catTraits = catTraitRepo.findTraitsByCategoryId(categoryId);
		model.addAttribute("traits", catTraits);
		var traitsToAdd = traitRepo.findAll().stream().filter(t -> !catTraits.contains(t)).collect(Collectors.toUnmodifiableSet());
		model.addAttribute("allTraits", traitsToAdd);
		return "category-admin";
	}
	
	@PostMapping("/admin/toggle_cat_is_active")
	public String toggleCategoryActive(@RequestParam Category cat) {
		cat.setActive(!cat.isActive());
		catRepo.saveAndFlush(cat);
		return "redirect:" + ADMIN_CATEGORIES_PATH;
	}
	
	@PostMapping("/admin/add_cat")
	public String addCategory(@RequestParam String name, @RequestParam boolean active) {
		if (name.trim().isEmpty()) {
			return "redirect:" + ADMIN_CATEGORIES_PATH + "?errorMessage=noname";
		}
		Category category = new Category(name, active);
		catRepo.saveAndFlush(category);
		return "redirect:" + ADMIN_CATEGORIES_PATH;
	}
	
	@PostMapping("/admin/update_cat")
	public String updateCategory(@RequestParam int id, @RequestParam String name, @RequestParam boolean active) {
		if (name.trim().isEmpty()) {
			return "redirect:" + ADMIN_CATEGORY_PATH + "?categoryId=" + id + "&errorMessage=noname";
		}
		Category category = catRepo.findById(id).get();
		catRepo.saveAndFlush(category.setName(name).setActive(active));
		return "redirect:" + ADMIN_CATEGORY_PATH + "?categoryId=" + id;
	}
	
	@PostMapping("/admin/add_cat_trait")
	public String addTraitToCategory(@RequestParam int traitId, @RequestParam int categoryId) {
		catTraitRepo.addTraitToCategory(categoryId, traitId);
		return "redirect:" + ADMIN_CATEGORY_PATH + "?categoryId=" + categoryId;
	}
	
	@PostMapping("/admin/remove_cat_trait")
	public String removeTraitFromCategory(@RequestParam int traitId, @RequestParam int categoryId) {
		catTraitRepo.deleteByCategoryIdAndTraitId(categoryId, traitId);
		return "redirect:" + ADMIN_CATEGORY_PATH + "?categoryId=" + categoryId;
	}
}
