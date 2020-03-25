package com.myshop.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myshop.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
	public Set<Item> findItemsByCategoryId(int categoryId);
	public List<Item> findItemsByCategoryId(int categoryId, Pageable page);
}
