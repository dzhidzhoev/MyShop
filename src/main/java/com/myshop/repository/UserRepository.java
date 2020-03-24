package com.myshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myshop.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
