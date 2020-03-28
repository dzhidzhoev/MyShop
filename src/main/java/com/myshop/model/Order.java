package com.myshop.model;

import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;

@Entity
@Table(name = "OrderTable")
@TypeDef(name = "pgsql_enum",
typeClass = PostgreSQLEnumType.class
)
public class Order {
	@Id
	@Column(name = "OrderID", unique = true, nullable = false)
	@SequenceGenerator(name = "OrderTable_OrderID_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OrderTable_OrderID_seq")
	private int id;
	@ManyToOne
	@JoinColumn(name = "UserID", nullable = false)
	private User user;
	private Timestamp orderTime;
	private String deliveryTime;
	private String name, phone, email, address, comment;
	@NotNull
	@Enumerated(EnumType.STRING)
	@Type(type = "pgsql_enum")
	private OrderStatus status;
	@NotNull
	private int total;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
	private Set<OrderItem> items;
	
	public int getId() {
		return id;
	}
	public Order setId(int id) {
		this.id = id;
		return this;
	}
	public User getUser() {
		return user;
	}
	public Order setUser(User user) {
		this.user = user;
		return this;
	}
	public Timestamp getOrderTime() {
		return orderTime;
	}
	public Order setOrderTime(Timestamp orderTime) {
		this.orderTime = orderTime;
		return this;
	}
	public String getDeliveryTime() {
		return deliveryTime;
	}
	public Order setDeliveryTime(String deliveryTime) {
		this.deliveryTime = deliveryTime;
		return this;
	}
	public String getName() {
		return name;
	}
	public Order setName(String name) {
		this.name = name;
		return this;
	}
	public String getPhone() {
		return phone;
	}
	public Order setPhone(String phone) {
		this.phone = phone;
		return this;
	}
	public String getEmail() {
		return email;
	}
	public Order setEmail(String email) {
		this.email = email;
		return this;
	}
	public String getAddress() {
		return address;
	}
	public Order setAddress(String address) {
		this.address = address;
		return this;
	}
	public String getComment() {
		return comment;
	}
	public Order setComment(String comment) {
		this.comment = comment;
		return this;
	}
	public OrderStatus getStatus() {
		return status;
	}
	public Order setStatus(OrderStatus status) {
		this.status = status;
		return this;
	}
	public int getTotal() {
		return total;
	}
	public Order setTotal(int total) {
		this.total = total;
		return this;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		Order other = (Order) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
