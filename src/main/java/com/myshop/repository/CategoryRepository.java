package com.myshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myshop.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
	public List<Category> findByNameContaining(String name);
}
