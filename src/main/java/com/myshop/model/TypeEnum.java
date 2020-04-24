package com.myshop.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TypeEnum {
	IntType, StringType, EnumType;
	
	public static final List<String> stringValues = Arrays.stream(values()).map(v -> v.toString()).collect(Collectors.toList());
}
