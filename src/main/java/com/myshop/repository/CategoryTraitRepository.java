package com.myshop.repository;

import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.myshop.model.CategoryTrait;
import com.myshop.model.Trait;

@Repository
public interface CategoryTraitRepository extends JpaRepository<CategoryTrait, Integer> {
	public Set<CategoryTrait> findByCategoryId(int categoryId);
	@Modifying
	@Transactional
	@Query(nativeQuery = true)
	public void deleteByCategoryIdAndTraitId(int categoryId, int traitId);
	@Transactional
	@Modifying
	@Query(value = "insert into CategoryTrait (CategoryID, TraitID) values (:cat, :trait)", nativeQuery = true)
	public void addTraitToCategory(@Param("cat") int categoryId, @Param("trait") int traitId);
	
	@Transactional
	public default Set<Trait> findTraitsByCategoryId(int categoryId) { 
		return findByCategoryId(categoryId).stream().map(ct -> ct.getTrait()).collect(Collectors.toSet());
	}
}
