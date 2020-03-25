package com.myshop;

import static org.testng.Assert.assertNotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.myshop.repository.CartRepository;
import com.myshop.repository.CategoryRepository;
import com.myshop.repository.ItemRepository;
import com.myshop.repository.ItemTraitRepository;
import com.myshop.repository.OrderItemRepository;
import com.myshop.repository.OrderRepository;
import com.myshop.repository.TraitRepository;
import com.myshop.repository.UserRepository;

@SpringBootTest
@TestPropertySource(value = "classpath:test_validate.properties")
public class MyShopDBValidaitonTest extends AbstractTestNGSpringContextTests {
	@Autowired CategoryRepository catRepo;
	@Autowired ItemRepository itemRepo;
	@Autowired OrderRepository orderRepo;
	@Autowired TraitRepository traitRepo;
	@Autowired UserRepository userRepo;
	@Autowired ItemTraitRepository itemTraitRepo;
	@Autowired CartRepository cartRepo;
	@Autowired OrderItemRepository orderItemRepo;
	
	@Test
	public void contextLoads() {
		assertNotNull(catRepo);
		assertNotNull(itemRepo);
		assertNotNull(orderRepo);
		assertNotNull(traitRepo);
		assertNotNull(userRepo);
		assertNotNull(itemTraitRepo);
		assertNotNull(cartRepo);
		assertNotNull(orderItemRepo);
	}
}
