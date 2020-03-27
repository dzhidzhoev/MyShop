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
import javax.persistence.SequenceGenerator;
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
	@SequenceGenerator(name = "Trait_TraitID_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Trait_TraitID_seq")
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
			@JoinColumn(name = "TraitID")
	}, inverseJoinColumns = {
			@JoinColumn(name = "CategoryID")
	})
	private List<Category> categories;
	
	public String getName() {
		return name;
	}
	public Trait setName(String text) {
		this.name = text;
		return this;
	}
	public TypeEnum getType() {
		return type;
	}
	public Trait setType(TypeEnum type) {
		this.type = type;
		return this;
	}
	public Boolean isSearchable() {
		return isSearchable;
	}
	public Trait setSearchable(boolean isSearchable) {
		this.isSearchable = isSearchable;
		return this;
	}
	public Integer getMinValue() {
		return minValue;
	}
	public Trait setMinValue(Integer minValue) {
		this.minValue = minValue;
		return this;
	}
	public String getUnit() {
		return unit;
	}
	public Trait setUnit(String unit) {
		this.unit = unit;
		return this;
	}
	public Integer getMaxValue() {
		return maxValue;
	}
	public Trait setMaxValue(Integer maxValue) {
		this.maxValue = maxValue;
		return this;
	}
	public List<String> getValues() {
		return values;
	}
	public Trait setValues(List<String> values) {
		this.values = values;
		return this;
	}
	public int getId() {
		return id;
	}
	public Trait setId(int id) {
		this.id = id;
		return this;
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
