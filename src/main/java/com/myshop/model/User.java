package com.myshop.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class User {
	@Id
	@Column(name = "UserID", nullable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@Column(unique = true, nullable = false)
	private String email;
	@Column(nullable = false)
	private String pwdHash;
	private String emailToken;
	private String pwdChangeToken;
	private boolean isAdmin, isDeleted;
	private String firstName, lastName, middleName;
	private String phone, address;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
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
	public boolean isAdmin() {
		return isAdmin;
	}
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	public boolean isDeleted() {
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
