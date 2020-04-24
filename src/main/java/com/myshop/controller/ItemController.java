package com.myshop.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.myshop.model.CategoryTrait;
import com.myshop.model.Item;
import com.myshop.model.Trait;
import com.myshop.model.TypeEnum;
import com.myshop.repository.CategoryRepository;
import com.myshop.repository.CategoryTraitRepository;
import com.myshop.repository.ItemRepository;
import com.myshop.repository.ItemTraitRepository;
import com.myshop.repository.TraitRepository;

import javassist.NotFoundException;

@Controller
public class ItemController {
	@Autowired ItemRepository itemRepo;
	@Autowired CategoryTraitRepository catTraitRepo;
	@Autowired ItemTraitRepository itemTraitRepo;
	@Autowired TraitRepository traitRepo;
	
	@RequestMapping(value="/item",method=RequestMethod.GET)
	public String view(Model model, @RequestParam int id) throws NotFoundException {
		Item item = itemRepo.findById(id).get();
		if (!item.isActive()) {
			throw new NotFoundException("No value present");
		}
		model.addAttribute("tRepo", traitRepo);
		model.addAttribute("itRepo", itemTraitRepo);
		model.addAttribute("item", item);
		var categoryId = item.getCategory().getId();
		Set<CategoryTrait> catTrait = catTraitRepo.findByCategoryId(categoryId);
		var traits = itemTraitRepo.findByItemId(id).stream()
			.filter(tr -> catTrait.contains(new CategoryTrait(new CategoryTrait.ID(categoryId, tr.getId().getTraitID()))))
			.filter(tr -> {
				if (tr.getTrait().getType() != TypeEnum.EnumType) {
					return tr.getTrait().getType() != TypeEnum.StringType || tr.getValue() != null;
				}
				if (tr.getTrait().getValues() == null) {
					return false;
				}
				return tr.getTrait().getValues().contains(tr.getValue());
			}).collect(Collectors.toList());
		model.addAttribute("traits", traits);
		return "item";
	}
	
	@GetMapping(value="/item_image")
	@ResponseBody
	public byte[] getImage(@RequestParam int id) throws IOException {
		var item = itemRepo.findById(id);
		if (item.isPresent() && !item.get().isActive()) {
			return defaultItemImage();
		}
		var img = itemRepo.getImageById(id);
		return img == null ? defaultItemImage() : img;
	}
	
	private static byte[] defaultItemImage() throws IOException {
		return Files.readAllBytes(new ClassPathResource("static/default.png").getFile().toPath());
	}
}
