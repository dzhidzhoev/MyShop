package com.myshop.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myshop.model.CategoryTrait;
import com.myshop.model.Item;
import com.myshop.model.ItemTrait;
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
	@Autowired CategoryRepository catRepo;
	@Autowired CategoryTraitRepository catTraitRepo;
	@Autowired ItemTraitRepository itemTraitRepo;
	@Autowired TraitRepository traitRepo;
	@Autowired CommonController common;
	
	private Gson gson = new Gson();
	
	@RequestMapping(value="/item",method=RequestMethod.GET)
	public String view(Model model, @RequestParam int id) throws NotFoundException {
		Item item = itemRepo.findById(id).get();
		if (!item.isActive() && !common.isUserAdmin()) {
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
	

	
	@GetMapping("/admin/item")
	public String showEditItem(Model model, @RequestParam int id) throws IOException {
		var item = itemRepo.findWithImageById(id).get();
		if (item.getImage() == null) {
			item.setImage(defaultItemImage());
		}
		model.addAttribute("item", item);
		model.addAttribute("imageString", Base64.getEncoder().encodeToString(item.getImage()));
		return "itemdata";
	}
	
	@GetMapping("/admin/edit_item_traits")
	public String showEditItemTraits(Model model, @RequestParam int id) {
		var item = itemRepo.findById(id).get();
		model.addAttribute("catTraits", catTraitRepo.findByCategoryId(item.getCategory().getId()));
		HashMap<Integer, ItemTrait> its = new HashMap<>();
		for (var it: itemTraitRepo.findByItemId(id)) {
			its.put(it.getTrait().getId(), it);
		}
		model.addAttribute("tRepo", traitRepo);
		model.addAttribute("itRepo", itemTraitRepo);
		model.addAttribute("itemTraits", its);
		model.addAttribute("itemId", id);
		return "itemtraits";
	}
	
	@PostMapping("/admin/edit_item_traits")
	public String editItemTraits(@RequestParam("itemTraits") String itemTraitsJson) throws UnsupportedEncodingException {
		var itemTraits = (ItemTrait[]) gson.fromJson(itemTraitsJson, new TypeToken<ItemTrait[]>() {}.getType());
		String errorMessage = null;
		for (var it : itemTraits) {
			if (it.getValue() == null && it.getValueIntOrNull() == null) {
				itemTraitRepo.delete(it);
				itemTraitRepo.flush();
			} else {
				var attempt = itemTraitRepo.setValue(it, itemTraitRepo.getValue(it, traitRepo, false), traitRepo, itemRepo);
				if (attempt.isEmpty()) {
					errorMessage = "Ошибка! Проверьте корректность значений";
				}
			}
		}
		return "redirect:/admin/item?id=" + itemTraits[0].getId().getItemID() +
				(errorMessage == null ? "" : "&errorMessage=" + URLEncoder.encode(errorMessage, "UTF-8"));
	}
	
	@PostMapping("/admin/update_item")
	public String updateItem(Item item, @RequestParam Optional<String> itemImage) throws UnsupportedEncodingException {
		var attemp = itemRepo.addOrUpdateItem(item.getId(),
				item.getCategory(),
				item.getName(),
				item.getPrice(),
				item.getCount(),
				item.isActive(),
				item.getDescription(),
				itemImage.map(s -> Base64.getDecoder().decode(s)).orElse(itemRepo.findById(item.getId()).get().getImage()));
		if (!attemp.getFirst().isPresent()) {
			return "redirect:/admin/item?id=" + item.getId() + "&errorMessage=" + URLEncoder.encode(attemp.getSecond(), "UTF-8");
		}
		return "redirect:/admin/item?id=" + item.getId();
	}
	
	@PostMapping("/admin/add_item")
	public String addItem(int categoryId) {
		var cat = catRepo.findById(categoryId).get();
		var item = new Item().setCategory(cat).setName("Новый товар " + cat.getName()).setActive(false);
		item = itemRepo.save(item);
		return "redirect:/admin/item?id=" + item.getId();
	}
	
	@GetMapping(value="/item_image")
	@ResponseBody
	public byte[] getImage(@RequestParam int id) throws IOException {
		var item = itemRepo.findById(id);
		if (item.isPresent() && !item.get().isActive() && !common.isUserAdmin()) {
			return defaultItemImage();
		}
		var img = itemRepo.getImageById(id);
		return img == null ? defaultItemImage() : img;
	}
	
	private static byte[] defaultItemImage() throws IOException {
		return Files.readAllBytes(new ClassPathResource("static/default.png").getFile().toPath());
	}
}
