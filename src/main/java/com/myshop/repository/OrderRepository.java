package com.myshop.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myshop.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
	public Set<Order> findByUserId(int userId);
	public List<Order> findByUserId(int userId, Pageable page);
}
