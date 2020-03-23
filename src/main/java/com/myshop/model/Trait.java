package com.myshop.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Trait {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "TraitID", unique = true, nullable = false)
	private long id;
	@NotNull
	private String name;
	private boolean isSearchable;
	@NotNull
	private TypeEnum type;
	private int minValue, maxValue;
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
	public boolean isSearchable() {
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
}
