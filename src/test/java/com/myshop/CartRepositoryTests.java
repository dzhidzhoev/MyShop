package com.myshop;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.myshop.model.Cart.ID;
import com.myshop.repository.CartRepository;
import com.myshop.repository.CategoryRepository;
import com.myshop.repository.ItemRepository;
import com.myshop.repository.ItemTraitRepository;
import com.myshop.repository.OrderRepository;
import com.myshop.repository.TraitRepository;
import com.myshop.repository.UserRepository;

@SpringBootTest
@TestPropertySource(value = "classpath:test.properties")
public class CartRepositoryTests extends AbstractTestNGSpringContextTests {
	@Autowired CategoryRepository catRepo;
	@Autowired ItemRepository itemRepo;
	@Autowired OrderRepository orderRepo;
	@Autowired TraitRepository traitRepo;
	@Autowired UserRepository userRepo;
	@Autowired ItemTraitRepository itemTraitRepo;
	@Autowired CartRepository cartRepo;
	
	@Test
	public void cartCrudTest() {
		var USER_ID = 2;
		var ITEM_ID = 1;
		var id = new ID(USER_ID, ITEM_ID);
		assertFalse(cartRepo.findById(id).isPresent());
		cartRepo.addToCart(USER_ID, ITEM_ID);
		assertTrue(cartRepo.findById(id).isPresent());
		var cartItem = cartRepo.findById(id).get();
		assertEquals(cartItem.getCount(), 1);
		assertEquals(cartItem.getId().getUserID(), 2);
		assertEquals(cartItem.getId().getItemID(), 1);
		
		cartItem = cartRepo.saveAndFlush(cartItem.setCount(cartItem.getCount() + 1));
		cartItem = cartRepo.findById(id).get();
		assertEquals(cartItem.getCount(), 2);
		assertEquals(cartItem.getId().getUserID(), 2);
		assertEquals(cartItem.getId().getItemID(), 1);
		
		cartRepo.delete(cartItem);
		cartRepo.flush();
		assertFalse(cartRepo.findById(id).isPresent());
		
		cartRepo.addToCart(USER_ID, ITEM_ID, 10);
		assertTrue(cartRepo.findById(id).isPresent());
		cartRepo.delete(cartItem);
		cartRepo.flush();
		assertFalse(cartRepo.findById(id).isPresent());
	}
}
