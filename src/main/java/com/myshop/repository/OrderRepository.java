package com.myshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myshop.model.Item;

@Repository
public interface OrderRepository extends JpaRepository<Item, Integer> {

}
