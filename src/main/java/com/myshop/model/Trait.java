package com.myshop.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.array.ListArrayType;

@Entity
@TypeDef(name = "list-array",
		typeClass = ListArrayType.class)
public class Trait {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "TraitID", unique = true, nullable = false)
	private int id;
	@NotNull
	private String name;
	private Boolean isSearchable;
	@NotNull
	@Enumerated(EnumType.STRING)
	private TypeEnum type;
	private int minValue, maxValue;
	@Type(type = "list-array")
	@Column(name = "Values", columnDefinition = "string[]")
	private List<String> values;
	private String unit;
	
	public String getName() {
		return name;
	}
	public void setName(String text) {
		this.name = text;
	}
	public TypeEnum getType() {
		return type;
	}
	public void setType(TypeEnum type) {
		this.type = type;
	}
	public Boolean isSearchable() {
		return isSearchable;
	}
	public void setSearchable(boolean isSearchable) {
		this.isSearchable = isSearchable;
	}
	public int getMinValue() {
		return minValue;
	}
	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public int getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}
	public List<String> getValues() {
		return values;
	}
	public void setValues(List<String> values) {
		this.values = values;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
