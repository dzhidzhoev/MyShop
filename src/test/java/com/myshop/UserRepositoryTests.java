package com.myshop;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.DataProvider;
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
	public void getPasswordHashTest() {
		assertEquals(userRepo.getPasswordHash(""), "0e1548cf13db518d04305dc1b0005d3c");
		assertEquals(userRepo.getPasswordHash("something"), "7d8eb8375d1573f6892e4cb951231503");
	}
	
	@Test
	public void logInTest() {
		// all ok
		assertEquals(userRepo.logIn("1@1.com", "1").get().getId(), 1);
		// other case
		assertEquals(userRepo.logIn("1@1.cOM", "1").get().getId(), 1);
		// spaces
		assertEquals(userRepo.logIn("   1@1.cOM\t", "1").get().getId(), 1);
		// invalid password
		assertEquals(userRepo.logIn("1@1.com", "2").isPresent(), false);
		// unknown user
		assertEquals(userRepo.logIn("-1@1.com", "1").isPresent(), false);
		// deleted user
		userRepo.saveAndFlush(userRepo.findById(1).get().setDeleted(true));
		assertEquals(userRepo.logIn("1@1.com", "1").isPresent(), false);
		userRepo.saveAndFlush(userRepo.findById(1).get().setDeleted(null));
		assertEquals(userRepo.logIn("1@1.com", "1").get().getId(), 1);
	}
	
	@Test
	public void isPasswordValidTest() {
		assertTrue(userRepo.isPasswordValid("abcdef123"));
		assertTrue(userRepo.isPasswordValid("abcde!\"#$%&\'()+-,*./f123"));
		assertFalse(userRepo.isPasswordValid("  abcdef123"));
		assertFalse(userRepo.isPasswordValid("abcdef123 "));
		assertTrue(userRepo.isPasswordValid("abcdef12"));
		assertFalse(userRepo.isPasswordValid("abcdef1"));
		assertFalse(userRepo.isPasswordValid("abcdef"));
		assertFalse(userRepo.isPasswordValid("{abcdef12345"));
		assertFalse(userRepo.isPasswordValid(""));
	}
	
	private static class Suit<T> {
		T val;
		T result;
		boolean good;
		public Suit(T val, T result, boolean good) {
			super();
			this.val = val;
			this.result = result;
			this.good = good;
		}
	}
	
	private void fillInternal(ArrayList<Integer> choice, List<List<Suit<? extends Object>>> vals,
			ArrayList<ArrayList<Object>> res) {
		if (choice.size() == vals.size()) {
			boolean result = true;
			for (int i = 0; i < vals.size(); i++) {
				result = result && vals.get(i).get(choice.get(i)).good;
			}
			var newSet = new ArrayList<>();
			for (int i = 0; i < choice.size(); i++) {
				newSet.add(vals.get(i).get(choice.get(i)).val);
			}
			for (int i = 0; i < choice.size(); i++) {
				newSet.add(vals.get(i).get(choice.get(i)).result);
			}
			newSet.add(result);
			res.add(newSet);
			return;
		}
		if (choice.size() > vals.size()) {
			return;
		}
		for (int i = 0; i < vals.get(choice.size()).size(); i++) {
			choice.add(i);
			fillInternal(choice, vals, res);
			choice.remove(choice.size() - 1);
		}
	}
	
	protected void fill(List<List<Suit<? extends Object>>> vals,
			ArrayList<ArrayList<Object>> res) {
		fillInternal(new ArrayList<>(), vals, res);
	}
	
	@DataProvider(name = "register")
	public Object[][] generateRegisterTestData() {
		var names = Lists.list(
				new Suit<>(null, null, false),
				new Suit<>("", "", false),
				new Suit<>("Иванов", "Иванов", true),
				new Suit<>("    Ivanov  ", "Ivanov", true));
		var firstNames = names;
		var lastNames = names;
		var middleNames = Lists.list(
				new Suit<>(null, null, true),
				new Suit<>("", "", true),
				new Suit<>("Иванов", "Иванов", true),
				new Suit<>("    Ivanov  ", "Ivanov", true));
		var valNull = Lists.list(
				new Suit<>(null, null, true),
				new Suit<>("val", "val", true));
		var phoneNumber = valNull;
		var address = valNull;
		var password = Lists.list(
				new Suit<>(null, null, false),
				new Suit<>("", "", false),
				new Suit<>("1", null, false),
				new Suit<>("abcdef123", userRepo.getPasswordHash("abcdef123"), true));
		var before = Lists.list(
			lastNames,
			firstNames,
			middleNames,
			phoneNumber, 
			address,
			password
		);
		var resultList = new ArrayList<ArrayList<Object>>();
		fill(before, resultList);
		Object[][] res = new Object[resultList.size()][];
		for (int i = 0; i < resultList.size(); i++) {
			res[i] = resultList.get(i).toArray();
		}
		return res;
	}

	public void registerUserTestEmail(String lastName, String firstName, String middleName, 
			String phoneNumber, String address, String email, String password,
			String lastNameRes, String firstNameRes, String middleNameRes, 
			String phoneNumberRes, String addressRes, String emailRes, String passwordRes, boolean good) {
		var userRes = userRepo.registerUser(lastName, firstName, middleName, phoneNumber, address, email, password);
		if (userRes.getFirst().isPresent()) {
			userRepo.deleteById(userRes.getFirst().get().getId());
			userRepo.flush();
		}
		assertEquals(userRes.getFirst().isPresent(), good);
		if (userRes.getFirst().isPresent()) {
			var user = userRes.getFirst().get();
			assertTrue(user.getEmailToken() != null);
			assertEquals(user.getLastName(), lastNameRes);
			assertEquals(user.getFirstName(), firstNameRes);
			assertEquals(user.getMiddleName(), middleNameRes);
			assertEquals(user.getPhone(), phoneNumberRes);
			assertEquals(user.getAddress(), addressRes);
			assertEquals(user.getEmail(), emailRes);
			assertEquals(user.getPwdHash(), passwordRes);
		}
	}
	
	@Test(dataProvider = "register")
	public void registerUserTest(String lastName, String firstName, String middleName, 
			String phoneNumber, String address, String password,
			String lastNameRes, String firstNameRes, String middleNameRes, 
			String phoneNumberRes, String addressRes, String passwordRes, boolean good) {
		// all ok
		registerUserTestEmail(lastName, firstName, middleName, phoneNumber, address, "100@100.com", password, lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, "100@100.com", passwordRes, good);
		// spaces
		registerUserTestEmail(lastName, firstName, middleName, phoneNumber, address, "   100@100.com\t", password, lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, "100@100.com", passwordRes, good);
		// invalid
		registerUserTestEmail(lastName, firstName, middleName, phoneNumber, address, "100100.com", password, lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, null, passwordRes, false);
		registerUserTestEmail(lastName, firstName, middleName, phoneNumber, address, "", password, lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, null, passwordRes, false);
		registerUserTestEmail(lastName, firstName, middleName, phoneNumber, address, null, password, lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, null, passwordRes, false);
		// already used
		registerUserTestEmail(lastName, firstName, middleName, phoneNumber, address, "1@1.com", password, lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, null, passwordRes, false);
		// check all deleted
		Arrays.deepEquals(userRepo.findAll(Sort.by(Order.asc("email"))).toArray(),
					new String[] {"1@1.com", "2@2.com", "3@3.com"});
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
	public void orderingAvailableTest() {
		assertTrue(userRepo.orderingAvailable(1));
		userRepo.saveAndFlush(userRepo.findById(1).get()
				.setEmailToken("smth"));
		assertFalse(userRepo.orderingAvailable(1));
		userRepo.saveAndFlush(userRepo.findById(1).get()
				.setEmailToken(null));
		assertTrue(userRepo.orderingAvailable(1));
		assertFalse(userRepo.orderingAvailable(0));
	}
	
	@Test
	public void approveEmailTest() {
		assertTrue(userRepo.orderingAvailable(1));
		userRepo.saveAndFlush(userRepo.findById(1).get()
				.setEmailToken("smth"));
		assertFalse(userRepo.orderingAvailable(1));
		userRepo.approveEmail("smth");
		assertTrue(userRepo.orderingAvailable(1));
		
		assertTrue(userRepo.orderingAvailable(1));
		assertTrue(userRepo.orderingAvailable(2));
		userRepo.saveAndFlush(userRepo.findById(1).get()
				.setEmailToken("smth"));
		userRepo.saveAndFlush(userRepo.findById(2).get()
				.setEmailToken("smth2"));
		userRepo.approveEmail("smth3");
		userRepo.approveEmail("");
		userRepo.approveEmail(null);
		assertFalse(userRepo.orderingAvailable(1));
		assertFalse(userRepo.orderingAvailable(2));
		userRepo.approveEmail("smth");
		assertTrue(userRepo.orderingAvailable(1));
		assertFalse(userRepo.orderingAvailable(2));
		userRepo.approveEmail("smth");
		assertTrue(userRepo.orderingAvailable(1));
		assertFalse(userRepo.orderingAvailable(2));
		userRepo.approveEmail("smth2");
		assertTrue(userRepo.orderingAvailable(1));
		assertTrue(userRepo.orderingAvailable(2));
	}
	
	@Test
	public void removeUserTest() {
		assertFalse(userRepo.findById(1).get().isDeleted());
		userRepo.removeUser(1);
		assertFalse(userRepo.findAll().isEmpty());
		userRepo.findAll().forEach(user -> {
			if (user.getId() == 1) {
				assertTrue(user.isDeleted());
			} else {
				assertFalse(user.isDeleted());
			}
		});
		userRepo.saveAndFlush(userRepo.findById(1).get().setDeleted(null));
		assertFalse(userRepo.findById(1).get().isDeleted());
	}
	
	@Test
	public void resetPasswordTest() {
		// unknown or null token
		userRepo.resetPassword("unknowntoken", "somgoood100");
		userRepo.resetPassword("unknowntoken", null);
		userRepo.resetPassword(null, "unknowntoken");
		userRepo.findAll().forEach(user -> {
			assertEquals(user.getPwdHash(), "444d01eb0131025c0f712674662ecd25");
			assertEquals(user.getPwdChangeToken(), null);
		});
		userRepo.saveAndFlush(userRepo.findById(1).get()
				.setPwdChangeToken("token"));
		assertEquals(userRepo.findById(1).get().getPwdChangeToken(), "token");
		userRepo.findAll().forEach(user -> {
			assertEquals(user.getPwdHash(), "444d01eb0131025c0f712674662ecd25");
		});
		userRepo.resetPassword("unknowntoken", "somgoood100");
		userRepo.resetPassword("unknowntoken", null);
		assertEquals(userRepo.findById(1).get().getPwdChangeToken(), "token");
		userRepo.findAll().forEach(user -> {
			assertEquals(user.getPwdHash(), "444d01eb0131025c0f712674662ecd25");
		});
		userRepo.resetPassword("token", "invalid");
		assertEquals(userRepo.findById(1).get().getPwdChangeToken(), "token");
		userRepo.findAll().forEach(user -> {
			assertEquals(user.getPwdHash(), "444d01eb0131025c0f712674662ecd25");
		});
		//deleted user
		userRepo.removeUser(1);
		userRepo.resetPassword("token", "somgoood100");
		assertEquals(userRepo.findById(1).get().getPwdChangeToken(), "token");
		userRepo.findAll().forEach(user -> {
			assertEquals(user.getPwdHash(), "444d01eb0131025c0f712674662ecd25");
		});
		userRepo.saveAndFlush(userRepo.findById(1).get()
				.setDeleted(null));
		assertFalse(userRepo.findById(1).get().isDeleted());
		// ok
		userRepo.resetPassword("token", "somgoood100");
		assertEquals(userRepo.findById(1).get().getPwdChangeToken(), null);
		userRepo.findAll().forEach(user -> {
			if (user.getId() != 1) {
				assertEquals(user.getPwdHash(), "444d01eb0131025c0f712674662ecd25");
			} else {
				assertEquals(user.getPwdHash(), "8981226d526db3563f1eab42516e3ab7");
			}
		});
		userRepo.saveAndFlush(userRepo.findById(1).get().setPwdHash("444d01eb0131025c0f712674662ecd25"));
		assertEquals(userRepo.findById(1).get().getPwdHash(), "444d01eb0131025c0f712674662ecd25");
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
