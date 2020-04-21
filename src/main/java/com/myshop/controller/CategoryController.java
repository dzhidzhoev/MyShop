package com.myshop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.myshop.model.Category;
import com.myshop.repository.CategoryRepository;

@Controller
public class CategoryController {
	@Autowired CategoryRepository catRepo;
	
}
