package com.myshop.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.myshop.repository.ItemRepositoryCustom;
import com.myshop.repository.ItemRepositoryCustom.Term;
import com.myshop.repository.TraitRepository;

@Component
public class TermStringConverter implements Converter<ItemRepositoryCustom.Term, String> {
	@Autowired TraitRepository traitRepo;
	@Autowired ConversionService conversionService;
		
	@Override
	public String convert(Term source) {
		Map<String, String> data = new HashMap<>();
		data.put("type", source.type.toString());
		data.put("traitId", conversionService.convert(source.trait.getId(), String.class));
		switch (source.type) {
		case BETWEEN:
			data.put("min", String.valueOf(source.minSegmentVal));
			data.put("max", String.valueOf(source.maxSegmentVal));
			break;
		case ONE_OF:
			data.put("vals", conversionService.convert(source.oneOfValues.toArray(), String.class));
			break;
		default:
			throw new IllegalArgumentException("unknown trait type value " + source.type.toString());
		}
		return conversionService.convert(data, String.class);
	}
}
