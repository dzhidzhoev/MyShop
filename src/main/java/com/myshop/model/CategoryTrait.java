package com.myshop.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class CategoryTrait {
	@Id
	@ManyToOne
	@JoinColumn(name = "CategoryID")
	private Category category;
	@Id
	@ManyToOne
	@JoinColumn(name = "TraitID")
	private Trait trait;
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public Trait getTrait() {
		return trait;
	}
	public void setTrait(Trait trait) {
		this.trait = trait;
	} 
}
