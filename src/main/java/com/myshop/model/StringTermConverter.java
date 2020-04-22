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

import com.myshop.repository.ItemRepositoryCustom;
import com.myshop.repository.ItemRepositoryCustom.Term;
import com.myshop.repository.TraitRepository;

@Component
public class StringTermConverter implements Converter<String, ItemRepositoryCustom.Term> {
	@Autowired TraitRepository traitRepo;
	@Autowired ConversionService conversionService;
	private static Map<String, String> empty = Map.of();
	private static Set<String> emptySet = new HashSet<>();
		
	@SuppressWarnings("unchecked")
	@Override
	public Term convert(String source) {
		Map<String, String> data = conversionService.convert(source, empty.getClass());
		return new Term(Term.TermType.valueOf(data.get("type")), 
				traitRepo.findById(Integer.valueOf(data.get("traitId"))).get(), 
				data.containsKey("vals") ? conversionService.convert(data.get("vals"), emptySet.getClass()) : null, 
				data.containsKey("min") ? Integer.valueOf(data.get("min")) : 0, 
				data.containsKey("max") ? Integer.valueOf(data.get("max")) : 0);
	}
}
