package com.myshop.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
public class OrderItem {
	
	@Embeddable
	public static class ID implements Serializable {
		private static final long serialVersionUID = -754941172640009098L;
		
		private int orderID, itemID;

		public int getOrderID() {
			return orderID;
		}

		public void setOrderID(int orderID) {
			this.orderID = orderID;
		}

		public int getItemID() {
			return itemID;
		}

		public void setItemID(int priceID) {
			this.itemID = priceID;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + orderID;
			result = prime * result + itemID;
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
			if (orderID != other.orderID)
				return false;
			if (itemID != other.itemID)
				return false;
			return true;
		}
	}
	
	@EmbeddedId private ID itemTrait;
	
	@ManyToOne
	@MapsId("orderID")
	@JoinColumn(name = "OrderID")
	private Order order;
	
	@ManyToOne
	@MapsId("itemID")
	@JoinColumn(name = "ItemID")
	private Item item;
	private Integer price, count;
	
	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((item == null) ? 0 : item.hashCode());
		result = prime * result + ((order == null) ? 0 : order.hashCode());
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
		OrderItem other = (OrderItem) obj;
		if (item == null) {
			if (other.item != null)
				return false;
		} else if (!item.equals(other.item))
			return false;
		if (order == null) {
			if (other.order != null)
				return false;
		} else if (!order.equals(other.order))
			return false;
		return true;
	}
}
