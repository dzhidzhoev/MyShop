package com.myshop.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
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
	private Integer minValue, maxValue;
	@Type(type = "list-array")
	@Column(name = "Values", columnDefinition = "string[]")
	private List<String> values;
	private String unit;
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "CategoryTrait", joinColumns = {
			@JoinColumn(name = "CategoryID")
	}, inverseJoinColumns = {
			@JoinColumn(name = "TraitID")
	})
	@Fetch(FetchMode.JOIN)
	private List<Category> categories;
	
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
	public Integer getMinValue() {
		return minValue;
	}
	public void setMinValue(Integer minValue) {
		this.minValue = minValue;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public Integer getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(Integer maxValue) {
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Trait other = (Trait) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
