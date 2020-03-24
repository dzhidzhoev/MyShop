package com.myshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myshop.model.Trait;

@Repository
public interface TraitRepository extends JpaRepository<Trait, Integer> {

}
