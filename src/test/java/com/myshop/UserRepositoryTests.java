package com.myshop;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.myshop.model.User;
import com.myshop.repository.CategoryRepository;
import com.myshop.repository.ItemRepository;
import com.myshop.repository.ItemTraitRepository;
import com.myshop.repository.OrderRepository;
import com.myshop.repository.TraitRepository;
import com.myshop.repository.UserRepository;

@SpringBootTest
@TestPropertySource(value = "classpath:test.properties")
public class UserRepositoryTests extends AbstractTestNGSpringContextTests {
	@Autowired CategoryRepository catRepo;
	@Autowired ItemRepository itemRepo;
	@Autowired OrderRepository orderRepo;
	@Autowired TraitRepository traitRepo;
	@Autowired UserRepository userRepo;
	@Autowired ItemTraitRepository itemTraitRepo;
	
	@Test
	public void findByEmailTest() {
		var res = userRepo.findByEmailIgnoreCase("1@1.com").get();
		assertEquals(res.getId(), 1);
		var res2 = userRepo.findByEmailIgnoreCase("4@4.com");
		assertEquals(res2.isPresent(), false);
	}
	
	@Test
	public void userLogInTest() {
		var VALID_EMAIL = "1@1.com";
		var VALID_PWD = "1";
		// all ok
		var res = userRepo.logIn(VALID_EMAIL, VALID_PWD);
		assertEquals(res.get().getId(), 1);
		// not found
		res = userRepo.logIn("4@4.com", "1");
		assertFalse(res.isPresent());
		// incorrect password
		res = userRepo.logIn(VALID_EMAIL, "2");
		assertFalse(res.isPresent());
		// deleted user
		var user1 = userRepo.findByEmailIgnoreCase(VALID_EMAIL).get();
		userRepo.saveAndFlush(user1.setDeleted((true)));
		res = userRepo.logIn(VALID_EMAIL, VALID_PWD);
		assertFalse(res.isPresent());
		userRepo.saveAndFlush(user1.setDeleted((false)));
		res = userRepo.logIn(VALID_EMAIL, VALID_PWD);
		assertEquals(res.get().getEmail(), VALID_EMAIL);
		userRepo.saveAndFlush(user1.setDeleted(null));
		res = userRepo.logIn(VALID_EMAIL, VALID_PWD);
		assertEquals(res.get().getEmail(), VALID_EMAIL);
	}
	
	@Test
	public void userCrudTest() {
		var USER_EMAIL = "username@user.com";
		var user = userRepo.saveAndFlush(new User(USER_EMAIL, USER_EMAIL, USER_EMAIL, null, false, false, USER_EMAIL, null, null, null, null));
		user = userRepo.findByEmailIgnoreCase(USER_EMAIL).get();
		assertEquals(user.getEmail(), USER_EMAIL);
		
		var USER_LAST_NAME = "Иванов";
		userRepo.saveAndFlush(user.setLastName(USER_LAST_NAME));
		assertEquals(userRepo.findByEmailIgnoreCase(USER_EMAIL).get().getLastName(), USER_LAST_NAME); 
	
		userRepo.deleteById(user.getId());
		assertEquals(userRepo.findByEmailIgnoreCase(USER_EMAIL).isPresent(), false);
	}
}
