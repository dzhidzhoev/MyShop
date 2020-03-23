package com.myshop.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import org.hibernate.annotations.ColumnDefault;

@Entity
public class Cart {
	@Embeddable
	public static class ID implements Serializable {

		private static final long serialVersionUID = 4226230939006293553L;

		private long userID, itemID;

		public long getItemID() {
			return itemID;
		}

		public void setItemID(long itemID) {
			this.itemID = itemID;
		}

		public long getUserID() {
			return userID;
		}

		public void setUserID(long userID) {
			this.userID = userID;
		}
	}

	
	@EmbeddedId private ID data;
	@ManyToOne
	@MapsId("userID")
	private User user;

	@ManyToOne
	@MapsId("itemID")
	private Item item;
	
	@ColumnDefault(value = "1")
	private long count;

	public long getCount() {
		return count;
	}
	
	public void setCount(long count) {
		this.count = count;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
}
