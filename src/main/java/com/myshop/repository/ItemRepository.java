package com.myshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myshop.model.Order;

@Repository
public interface ItemRepository extends JpaRepository<Order, Long> {

}
