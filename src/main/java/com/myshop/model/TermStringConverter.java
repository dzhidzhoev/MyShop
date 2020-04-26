package com.myshop.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.myshop.repository.ItemRepositoryCustom;
import com.myshop.repository.ItemRepositoryCustom.Term;
import com.myshop.repository.TraitRepository;

@Component
public class TermStringConverter implements Converter<ItemRepositoryCustom.Term, String> {
	@Autowired TraitRepository traitRepo;
	@Autowired ConversionService conversionService;
	
	private Gson gson = new Gson();
		
	@Override
	public String convert(Term source) {
		return gson.toJson(source);
	}
}
