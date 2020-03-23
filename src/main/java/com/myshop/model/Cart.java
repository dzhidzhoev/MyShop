package com.myshop.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ColumnDefault;

@Entity
public class Cart {
	@Id
	@ManyToOne
	@JoinColumn(name = "UserID")
	private User user;
	@Id
	@ManyToOne
	@JoinColumn(name = "ItemID")
	private Item item;
	@ColumnDefault(value = "1")
	private long count;
}
