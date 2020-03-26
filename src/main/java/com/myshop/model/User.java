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
	
	public User() {
		this(null, null, null, null, false, false, null, null, null, null, null);
	}
	
	public User(String email, String pwdHash, String emailToken, String pwdChangeToken, boolean isAdmin,
			boolean isDeleted, String firstName, String lastName, String middleName, String phone, String address) {
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
	public User setId(int id) {
		this.id = id;
		return this;
	}
	public String getEmail() {
		return email;
	}
	public User setEmail(String email) {
		this.email = email;
		return this;
	}
	public String getPwdHash() {
		return pwdHash;
	}
	public User setPwdHash(String pwdHash) {
		this.pwdHash = pwdHash;
		return this;
	}
	public String getEmailToken() {
		return emailToken;
	}
	public User setEmailToken(String emailToken) {
		this.emailToken = emailToken;
		return this;
	}
	public String getPwdChangeToken() {
		return pwdChangeToken;
	}
	public User setPwdChangeToken(String pwdChangeToken) {
		this.pwdChangeToken = pwdChangeToken;
		return this;
	}
	public Boolean isAdminOrNull() {
		return isAdmin;
	}
	public User setAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
		return this;
	}
	public Boolean isDeletedOrNull() {
		return isDeleted;
	}
	
	public boolean isDeleted() {
		return Boolean.valueOf(true).equals(isDeleted);
	}
	
	public User setDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
		return this;
	}
	public String getFirstName() {
		return firstName;
	}
	public User setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}
	public String getLastName() {
		return lastName;
	}
	public User setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}
	public String getMiddleName() {
		return middleName;
	}
	public User setMiddleName(String middleName) {
		this.middleName = middleName;
		return this;
	}
	public String getPhone() {
		return phone;
	}
	public User setPhone(String phone) {
		this.phone = phone;
		return this;
	}
	public String getAddress() {
		return address;
	}
	public User setAddress(String address) {
		this.address = address;
		return this;
	}
}
