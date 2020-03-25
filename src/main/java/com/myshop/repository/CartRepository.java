package com.myshop.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myshop.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Cart.ID> {
	public Set<Cart> findByUserId(int userId);
}
