package com.myshop.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.myshop.repository.ItemRepositoryCustom.Term;

@Controller
public class ItemsController {
	@GetMapping("/")
	public String showItems(
			@RequestParam Optional<Integer> cateogryId,
			@RequestParam(name = "t") Optional<List<Term>> searchTerms
			) {
		return "items";
	}
}
