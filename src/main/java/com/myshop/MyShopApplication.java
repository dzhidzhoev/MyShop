package com.myshop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.myshop.repository.CategoryRepository;

@SpringBootApplication
@Controller
public class MyShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyShopApplication.class, args);
	}
	
	@Autowired
	CategoryRepository repo;
	
	@RequestMapping(value="/test",method=RequestMethod.GET)
	@ResponseBody
	public String test() {
		StringBuilder sb = new StringBuilder(repo.count() + "");
		repo.findAll().forEach((x) -> {
			sb.append("<br>" + x.getName());
		});
		return sb.toString();
	}

}
