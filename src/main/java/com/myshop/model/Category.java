package com.myshop.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "Category")
public class Category {
	@Id
	@SequenceGenerator(name = "Category_CategoryID_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Category_CategoryID_seq")
	@Column(name = "CategoryID", unique = true, nullable = false)
	private int id;
	private String name;
	@Column(name = "IsActive")
	private Boolean isActive;
	@OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
	private Set<Item> items;
	@ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
	private Set<Trait> traits;
	
	public Category() {}

	public Category(String name, Boolean isActive) {
		this.name = name;
		this.isActive = isActive;
	}


	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		if (name == null) {
			throw new NullPointerException("name cannot be null");
		}
		this.name = name;
	}
	
	public Boolean isActive() {
		return isActive;
	}
	
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
}
