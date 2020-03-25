package com.myshop.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myshop.model.ItemTrait;
import com.myshop.model.Trait;

@Repository
public interface TraitRepository extends JpaRepository<Trait, Integer> {
	public Set<Trait> findTraitsByCategories_Id(int categoryId);
}
