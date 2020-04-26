package com.myshop.model;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.myshop.repository.ItemRepositoryCustom;
import com.myshop.repository.ItemRepositoryCustom.Term;
import com.myshop.repository.TraitRepository;

@Component
public class StringTermConverter implements Converter<String, ItemRepositoryCustom.Term> {
	@Autowired TraitRepository traitRepo;
	@Autowired ConversionService conversionService;
	private static Map<String, String> empty = Map.of();
	private static Set<String> emptySet = new HashSet<>();
		
	private Gson gson = new Gson();
	
	@SuppressWarnings("unchecked")
	@Override
	public Term convert(String source) {
		return gson.fromJson(source, Term.class);
	}
}
