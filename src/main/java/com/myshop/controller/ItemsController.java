package com.myshop.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.myshop.model.Category;
import com.myshop.model.Item;
import com.myshop.model.Trait;
import com.myshop.model.TypeEnum;
import com.myshop.repository.CategoryRepository;
import com.myshop.repository.CategoryTraitRepository;
import com.myshop.repository.ItemRepository;
import com.myshop.repository.ItemRepositoryCustom.Term;
import com.myshop.repository.ItemTraitRepository;
import com.myshop.repository.TraitRepository;

import javassist.NotFoundException;

@Controller
public class ItemsController {
	@Autowired ItemRepository itemRepo;
	@Autowired CategoryTraitRepository catTraitRepo;
	@Autowired ItemTraitRepository itemTraitRepo;
	@Autowired TraitRepository traitRepo;
	@Autowired CategoryRepository catRepo;
	@Autowired CommonController common;
	
	@GetMapping("/")
	public String showItems(Model model,
			@RequestParam Optional<Integer> categoryId,
			@RequestParam(name = "searchTerms", required = false) Set<Term> searchTerms,
			@RequestParam Optional<Integer> minPrice, @RequestParam Optional<Integer> maxPrice
			) throws NotFoundException {
		boolean isAdmin = common.isUserAdmin();
		
		Collection<Item> items = null;
		Collection<Trait> catTraits = null;
		Category category = null;
		
		Map<Integer, Set<String>> stringTraitValues = new HashMap<>();
		
		if (categoryId.isEmpty()) {
			if (minPrice.isPresent() && maxPrice.isPresent()) {
				items = itemRepo.findByPriceBetween(minPrice.get(), maxPrice.get());
			} else if (minPrice.isPresent()) {
				items = itemRepo.findByPriceGreaterThanEqual(minPrice.get());
			} else if (maxPrice.isPresent()) {
				items = itemRepo.findByPriceLessThanEqual(maxPrice.get());
			} else {
				items = itemRepo.findAll();	
			}
		} else {
			category = catRepo.findById(categoryId.get()).get();
			if (!category.isActive()) {
				throw new NotFoundException("No value present");
			}
			catTraits = catTraitRepo.findByCategoryId(category.getId()).stream()
					.map(ct -> ct.getTrait())
					.filter(Trait::isSearchable)
					.collect(Collectors.toList());
			catTraits.forEach(t -> {
				if (t.getType() == TypeEnum.StringType) {
					stringTraitValues.put(t.getId(), itemTraitRepo.findStringValuesOfTrait(t.getId()));
				}
			});
			if (searchTerms == null || searchTerms.isEmpty()) {
				if (minPrice.isPresent() && maxPrice.isPresent()) {
					items = itemRepo.findItemsByCategoryIdAndPriceBetween(categoryId.get(), minPrice.get(), maxPrice.get());
				} else if (minPrice.isPresent()) {
					items = itemRepo.findItemsByCategoryIdAndPriceGreaterThanEqual(categoryId.get(), minPrice.get());
				} else if (maxPrice.isPresent()) {
					items = itemRepo.findItemsByCategoryIdAndPriceLessThanEqual(categoryId.get(), maxPrice.get());
				} else {
					items = itemRepo.findItemsByCategoryId(categoryId.get());
				}
			} else {
				items = itemRepo.findItemsByTerms(category, searchTerms, minPrice.orElse(null), maxPrice.orElse(null));
			}
		}
		items = items.stream().filter(i -> i.isActive() || isAdmin).collect(Collectors.toList());
		if (searchTerms != null) {
			var storage = new HashMap<Integer, Term>();
			for (var term: searchTerms) {
				storage.put(term.traitID, term);
			}
			model.addAttribute("searchTerms", storage);
		} else {
			model.addAttribute("searchTerms", null);
		}
		model.addAttribute("catTraits", catTraits);
		model.addAttribute("category", category);
		model.addAttribute("stringTraitValues", stringTraitValues);
		model.addAttribute("items", items);
		return "items";
	}
}
