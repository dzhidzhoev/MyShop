package com.myshop.repository;

import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.myshop.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Cart.ID> {
	public Set<Cart> findByUserId(int userId);
	
	public default void addToCart(int userId, int itemId) {
		addToCart(userId, itemId, 1);
	}
	@Transactional
	@Modifying
	@Query(value = "insert into Cart (UserID, ItemID, Count) values (:user, :item, :count)", nativeQuery = true)
	public void addToCart(@Param("user") int userId, @Param("item") int itemId, @Param("count") int count);
}
