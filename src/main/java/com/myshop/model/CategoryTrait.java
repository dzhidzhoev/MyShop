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

	public Category getCategory() {
		return category;
	}

	public Trait getTrait() {
		return trait;
	}

	@Embeddable
	public static class ID implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6425904834460082612L;


		public int categoryId, traitId;

		public ID() {}
		
		public ID(int categoryId, int traitId) {
			super();
			this.categoryId = categoryId;
			this.traitId = traitId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (categoryId ^ (categoryId >>> 32));
			result = prime * result + (int) (traitId ^ (traitId >>> 32));
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
			if (categoryId != other.categoryId)
				return false;
			if (traitId != other.traitId)
				return false;
			return true;
		}

		
	}
	
	@EmbeddedId private ID categoryTrait;
	
	@ManyToOne
	@MapsId("categoryID")
	@JoinColumn(name = "CategoryID")
	private Category category;

	@ManyToOne
	@MapsId("traitID")
	@JoinColumn(name = "TraitID")
	private Trait trait;
	
	public CategoryTrait() {
	}
	
	public CategoryTrait(ID id) {
		categoryTrait = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + categoryTrait.categoryId;
		result = prime * result + categoryTrait.traitId;
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
		if (categoryTrait.categoryId != other.categoryTrait.categoryId)
			return false;
		if (categoryTrait.traitId != other.categoryTrait.traitId)
			return false;
		return true;
	}
	
	
}
