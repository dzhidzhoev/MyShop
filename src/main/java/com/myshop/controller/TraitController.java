package com.myshop.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.myshop.model.Category;
import com.myshop.model.Trait;
import com.myshop.model.TypeEnum;
import com.myshop.repository.CategoryRepository;
import com.myshop.repository.CategoryTraitRepository;
import com.myshop.repository.TraitRepository;

@Controller
public class TraitController {
	public static final String MANAGE_TRAITS_PATH = "/admin/traits";
	
	@Autowired CategoryRepository catRepo;
	@Autowired CategoryTraitRepository catTraitRepo;
	@Autowired TraitRepository traitRepo;
	
	@GetMapping(MANAGE_TRAITS_PATH)
	public String showTraitsManagement(Model model) {
		model.addAttribute("traitsCount", traitRepo.count());
		int i = 0;
		for (Trait trait: traitRepo.findAll()) {
			model.addAttribute("trait_" + i, trait);
			++i;
		}
		model.addAttribute("types",  TypeEnum.stringValues);
		model.addAttribute("newTrait", new Trait());
		return "traits";
	}
	
	@PostMapping("/admin/add_update_trait")
	public String addOrUpdateTrait(
			Trait trait
			) throws UnsupportedEncodingException {
		var attempt = traitRepo.addOrUpdateTrait(trait.getId(), trait.getName(), trait.isSearchable(), trait.getType(), trait.getMinValue(), trait.getMaxValue(), trait.getValues(), trait.getUnit());
		if (!attempt.getFirst().isPresent()) {
			return "redirect:" + MANAGE_TRAITS_PATH + "?errorMessage=" + URLEncoder.encode(attempt.getSecond(), "UTF-8");
		}
		return "redirect:" + MANAGE_TRAITS_PATH;
	}
	
	@PostMapping("/admin/delete_trait")
	public String deleteTrait(int id) {
		traitRepo.deleteById(id);
		traitRepo.flush();
		return "redirect:" + MANAGE_TRAITS_PATH;
	}
}
