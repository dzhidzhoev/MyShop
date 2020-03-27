package com.myshop.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import com.myshop.model.Cart;
import com.myshop.model.Order;
import com.myshop.model.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
	public Set<Order> findByUserId(int userId);
	public List<Order> findByUserId(int userId, Pageable page);
	
//	public Pair<Optional<Order>, String> placeNewOrder(int userId, CartRepository cartRepo) {
//		
//	}
}
