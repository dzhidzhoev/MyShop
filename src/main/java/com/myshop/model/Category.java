package com.myshop.model;

import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.transaction.Transactional;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "Category")
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "CategoryID", unique = true, nullable = false)
	private int id;
	private String name;
	@Column(name = "IsActive")
	private Boolean isActive;
	@OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
	@Fetch(FetchMode.JOIN)
	private Set<Item> items;

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
	
	@Transactional
	public Set<Item> getItems() {
		return items;
	}
}
