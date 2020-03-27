package com.myshop.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myshop.model.ItemTrait;

public interface ItemTraitRepository extends JpaRepository<ItemTrait, ItemTrait.ID> {
	public Set<ItemTrait> findByItemId(int itemId);
}