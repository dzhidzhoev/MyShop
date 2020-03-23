package com.myshop.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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


		private long itemID, traitID;


		public long getTraitID() {
			return traitID;
		}


		public void setTraitID(long traitID) {
			this.traitID = traitID;
		}


		public long getItemID() {
			return itemID;
		}


		public void setItemID(long itemID) {
			this.itemID = itemID;
		}
	}
	
	@EmbeddedId private ID itemTrait;

	@ManyToOne
	@MapsId("itemID")
	private Item item;

	@ManyToOne
	@MapsId("traitID")
	private Trait trait;
	
	private String value;
	private long valueInt;
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public long getValueInt() {
		return valueInt;
	}
	public void setValueInt(long valueInt) {
		this.valueInt = valueInt;
	}
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Trait getTrait() {
		return trait;
	}

	public void setTrait(Trait trait) {
		this.trait = trait;
	}
	
}
