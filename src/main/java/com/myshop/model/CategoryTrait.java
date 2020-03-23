package com.myshop.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
public class CategoryTrait {
	@Embeddable
	public static class ID implements Serializable {

		private static final long serialVersionUID = 4684456376077857230L;
		
		private int categoryID, traitID;

		public int getCategoryID() {
			return categoryID;
		}

		public void setCategoryID(int categoryID) {
			this.categoryID = categoryID;
		}

		public int getTraitID() {
			return traitID;
		}
		
		public void setTraitID(int traitID) {
			this.traitID = traitID;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (categoryID ^ (categoryID >>> 32));
			result = prime * result + (int) (traitID ^ (traitID >>> 32));
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
			ID other = (ID) obj;
			if (categoryID != other.categoryID)
				return false;
			if (traitID != other.traitID)
				return false;
			return true;
		}
	}
	
	@EmbeddedId private ID id; 
	
	@ManyToOne
	@MapsId("categoryID")
	@JoinColumn(name = "CategoryID")
	private Category category;
	
	@ManyToOne
	@MapsId("traitID")
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		CategoryTrait other = (CategoryTrait) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	} 
}
