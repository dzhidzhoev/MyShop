package com.myshop;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.myshop.repository.CategoryRepository;
import com.myshop.repository.ItemRepository;
import com.myshop.repository.OrderRepository;
import com.myshop.repository.TraitRepository;
import com.myshop.repository.UserRepository;

@SpringBootTest
class MyShopApplicationTests {
	
	@Autowired CategoryRepository catRepo;
	@Autowired ItemRepository itemRepo;
	@Autowired OrderRepository orderRepo;
	@Autowired TraitRepository traitRepo;
	@Autowired UserRepository userRepo;
	
	@Test
	void contextLoads() {
		assertThat(catRepo).isNotNull();
		assertThat(itemRepo).isNotNull();
		assertThat(orderRepo).isNotNull();
		assertThat(traitRepo).isNotNull();
		assertThat(userRepo).isNotNull();
	}

}
