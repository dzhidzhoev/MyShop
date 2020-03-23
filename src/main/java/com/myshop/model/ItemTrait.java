package com.myshop.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ItemTrait {
	@Id
	@ManyToOne
	@JoinColumn(name = "ItemID")
	private Item item;
	@Id
	@ManyToOne
	@JoinColumn(name = "TraitID")
	private Trait trait;
	private String value;
	private long valueInt;
	
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
	
}
