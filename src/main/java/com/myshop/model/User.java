package com.myshop.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "UserTable")
public class User {
	@Id
	@Column(name = "UserID", nullable = false, unique = true)
	@SequenceGenerator(name = "usertable_userid_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usertable_userid_seq")
	private int id;
	@Column(unique = true, nullable = false)
	private String email;
	@Column(nullable = false)
	private String pwdHash;
	private String emailToken;
	private String pwdChangeToken;
	private Boolean isAdmin, isDeleted;
	private String firstName, lastName, middleName;
	private String phone, address;
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private Set<Cart> cart;
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private Set<Order> orders;
	
	public User() {}
	
	public User(String email, String pwdHash, String emailToken, String pwdChangeToken, Boolean isAdmin,
			Boolean isDeleted, String firstName, String lastName, String middleName, String phone, String address) {
		super();
		this.email = email;
		this.pwdHash = pwdHash;
		this.emailToken = emailToken;
		this.pwdChangeToken = pwdChangeToken;
		this.isAdmin = isAdmin;
		this.isDeleted = isDeleted;
		this.firstName = firstName;
		this.lastName = lastName;
		this.middleName = middleName;
		this.phone = phone;
		this.address = address;
	}


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPwdHash() {
		return pwdHash;
	}
	public void setPwdHash(String pwdHash) {
		this.pwdHash = pwdHash;
	}
	public String getEmailToken() {
		return emailToken;
	}
	public void setEmailToken(String emailToken) {
		this.emailToken = emailToken;
	}
	public String getPwdChangeToken() {
		return pwdChangeToken;
	}
	public void setPwdChangeToken(String pwdChangeToken) {
		this.pwdChangeToken = pwdChangeToken;
	}
	public Boolean isAdmin() {
		return isAdmin;
	}
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	public Boolean isDeleted() {
		return isDeleted;
	}
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
}
