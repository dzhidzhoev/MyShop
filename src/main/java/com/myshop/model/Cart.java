package com.myshop.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import org.hibernate.annotations.ColumnDefault;

@Entity
public class Cart {
	@Embeddable
	public static class ID implements Serializable {

		private static final long serialVersionUID = 4226230939006293553L;

		private int userID;
		private int itemID;

		public int getItemID() {
			return itemID;
		}

		public void setItemID(int itemID) {
			this.itemID = itemID;
		}

		public int getUserID() {
			return userID;
		}

		public void setUserID(int userID) {
			this.userID = userID;
		}
		
		@Override
		public int hashCode() {
			return userID ^ itemID;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ID) {
				return ((ID) obj).itemID == itemID && ((ID)obj).userID == userID;
			}
			return false;
		}
	}

	
	@EmbeddedId 
	@AttributeOverrides({
		@AttributeOverride(name = "userID", column = @Column(name = "UserID")),
		@AttributeOverride(name = "itemID", column = @Column(name = "ItemID")),
	})
	private ID data;
	@ManyToOne
	@MapsId("userID")
	@JoinColumn(name = "UserID")
	private User user;

	@ManyToOne
	@MapsId("itemID")
	@JoinColumn(name = "ItemID")
	private Item item;
	
	@ColumnDefault(value = "1")
	private int count;

	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
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
	
	@Override
	public int hashCode() {
		return data.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Cart) {
			return ((Cart) obj).data.equals(data);
		}
		return false;
	}
}
