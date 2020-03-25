package com.myshop.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myshop.model.Item;
import com.myshop.model.ItemTrait;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
	public Set<Item> findItemsByCategoryId(int categoryId);
}
