package com.myshop.model;

import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;

@Entity
public class Item {
	@Id
	@Column(name = "ItemID", unique = true, nullable = false)
	@SequenceGenerator(name = "Item_ItemID_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Item_ItemID_seq")
	private int id;
	@ManyToOne
	@JoinColumn(name = "CategoryID")
	private Category category;
	@NotNull
	private String name;
	@NotNull
	private int price;
	private int count;
	private Boolean active;
	private String description;
	@Basic(fetch = FetchType.LAZY)
	private byte[] image;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
	private Set<ItemTrait> traits;
	
	public Set<ItemTrait> getTraits() {
		return traits;
	}
	public int getId() {
		return id;
	}
	public Item setId(int id) {
		this.id = id;
		return this;
	}
	public Category getCategory() {
		return category;
	}
	public Item setCategory(Category category) {
		this.category = category;
		return this;
	}
	public String getName() {
		return name;
	}
	public Item setName(String name) {
		this.name = name;
		return this;
	}
	public int getPrice() {
		return price;
	}
	public Item setPrice(int price) {
		this.price = price;
		return this;
	}
	public int getCount() {
		return count;
	}
	public Item setCount(int count) {
		this.count = count;
		return this;
	}
	public boolean isActive() {
		return Boolean.valueOf(true).equals(active);
	}
	public Item setActive(boolean active) {
		this.active = active;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public Item setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public byte[] getImage() {
		return image;
	}
	
	public Item setImage(byte[] img) {
		this.image = img;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof Item) {
			return ((Item)obj).id == id;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return (int) id;
	}
}
