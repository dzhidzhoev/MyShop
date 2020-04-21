package com.myshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ItemController {
	@RequestMapping(value="/item",method=RequestMethod.GET)
	public String view(Model model, @RequestParam(value="id", required = true) int id) {
		model.addAttribute("id", id);
		return "item";
	}
}
