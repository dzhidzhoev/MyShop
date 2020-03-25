package com.myshop.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.myshop.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItem.ID> {
	public Set<OrderItem> findByOrderId(int orderId);
	public List<OrderItem> findByOrderId(int orderId, Pageable pageable);
}
