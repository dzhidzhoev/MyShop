package com.myshop.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
public class CategoryTrait {
	@Embeddable
	public static class ID implements Serializable {

		private static final long serialVersionUID = 4684456376077857230L;
		
		private long categoryID, traitID;

		public long getCategoryID() {
			return categoryID;
		}

		public void setCategoryID(long categoryID) {
			this.categoryID = categoryID;
		}

		public long getTraitID() {
			return traitID;
		}

		public void setTraitID(long traitID) {
			this.traitID = traitID;
		}
		
	}
	
	@EmbeddedId private ID id; 
	
	@ManyToOne
	@MapsId("categoryID")
	private Category category;
	
	@ManyToOne
	@MapsId("traitID")
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
