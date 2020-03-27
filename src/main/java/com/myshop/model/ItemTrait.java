package com.myshop.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
public class ItemTrait {
	
	@Embeddable
	public static class ID implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6425904834460082612L;


		private int itemID, traitID;
		
		public ID() {}
		
		
		public ID(int itemID, int traitID) {
			super();
			this.itemID = itemID;
			this.traitID = traitID;
		}


		public int getItemID() {
			return itemID;
		}


		public void setItemID(int itemID) {
			this.itemID = itemID;
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
			result = prime * result + (int) (itemID ^ (itemID >>> 32));
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
			if (itemID != other.itemID)
				return false;
			if (traitID != other.traitID)
				return false;
			return true;
		}
		
	}
	
	@EmbeddedId private ID itemTrait;

	@ManyToOne
	@MapsId("itemID")
	@JoinColumn(name = "ItemID")
	private Item item;

	@ManyToOne
	@MapsId("traitID")
	@JoinColumn(name = "TraitID")
	private Trait trait;
	
	private String value;
	private Integer valueInt;
	
	public String getValue() {
		return value;
	}
	public ItemTrait setValue(String value) {
		this.value = value;
		return this;
	}
	public int getValueInt() {
		return valueInt == null ? 0 : valueInt;
	}
	public ItemTrait setValueInt(Integer valueInt) {
		this.valueInt = valueInt;
		return this;
	}
	public Item getItem() {
		return item;
	}

	public Trait getTrait() {
		return trait;
	}
	
	public ItemTrait setItem(Item item) {
		this.item = item;
		return this;
	}
	
	public ItemTrait setTrait(Trait trait) {
		this.trait = trait;
		return this;
	}
	
	public ID getId() {
		return itemTrait;
	}
	
	public ItemTrait setId(ID itemTrait) {
		this.itemTrait = itemTrait;
		return this;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemTrait == null) ? 0 : itemTrait.hashCode());
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
		ItemTrait other = (ItemTrait) obj;
		if (itemTrait == null) {
			if (other.itemTrait != null)
				return false;
		} else if (!itemTrait.equals(other.itemTrait))
			return false;
		return true;
	}
	
}
