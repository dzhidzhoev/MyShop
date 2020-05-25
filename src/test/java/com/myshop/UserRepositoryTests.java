package com.myshop;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.util.Streams;
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
		// null
		assertEquals(userRepo.logIn(null, "1").isPresent(), false);
		assertEquals(userRepo.logIn("1@1.com", null).isPresent(), false);
		assertEquals(userRepo.logIn(null, null).isPresent(), false);
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
	
	public static class Suite<T> {
		T val;
		T result;
		boolean good;
		public Suite(T val, T result, boolean good) {
			super();
			this.val = val;
			this.result = result;
			this.good = good;
		}
	}
	
	private static void fillInternal(ArrayList<Integer> choice, List<List<Suite<? extends Object>>> vals,
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
	
	public static void fill(List<List<Suite<? extends Object>>> vals,
			ArrayList<ArrayList<Object>> res) {
		fillInternal(new ArrayList<>(), vals, res);
	}
	
	@DataProvider(name = "register")
	public Object[][] generateRegisterTestData() {
		var names = Lists.list(
				new Suite<>(null, null, false),
				new Suite<>("", "", false),
				new Suite<>("Иванов", "Иванов", true),
				new Suite<>("    Ivanov  ", "Ivanov", true));
		var firstNames = names;
		var lastNames = names;
		var middleNames = Lists.list(
				new Suite<>(null, null, true),
				new Suite<>("", "", true),
				new Suite<>("Иванов", "Иванов", true),
				new Suite<>("    Ivanov  ", "Ivanov", true));
		var valNull = Lists.list(
				new Suite<>(null, null, true),
				new Suite<>("val", "val", true));
		var phoneNumber = valNull;
		var address = valNull;
		var password = Lists.list(
				new Suite<>(null, null, true),
				new Suite<>("", "", true),
				new Suite<>("1", null, false),
				new Suite<>(INITIAL_PWD, userRepo.getPasswordHash(INITIAL_PWD), true),
				new Suite<>("abcdef123", userRepo.getPasswordHash("abcdef123"), true));
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

	public Integer registerUserTestEmail(Integer id, String lastName, String firstName, String middleName, 
			String phoneNumber, String address, String email, String password,
			String lastNameRes, String firstNameRes, String middleNameRes, 
			String phoneNumberRes, String addressRes, String emailRes, String passwordRes, boolean good) {
		return registerUserTestEmail(true, id, lastName, firstName, middleName, phoneNumber, address, email, password, lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, emailRes, passwordRes, good);
	}
	
	public Integer registerUserTestEmail(boolean delete, Integer id, String lastName, String firstName, String middleName, 
			String phoneNumber, String address, String email, String password,
			String lastNameRes, String firstNameRes, String middleNameRes, 
			String phoneNumberRes, String addressRes, String emailRes, String passwordRes, boolean good) {
		
		boolean pwdChanges = id != null && !(INITIAL_PWD.equals(password) || password == null || password.isEmpty());
		boolean emailChanges = id != null && !INITIAL_EMAIL.equals(email);
		good = good && !(pwdChanges && emailChanges);
		
		var userRes = userRepo.registerUser(id, lastName, firstName, middleName, phoneNumber, address, email, password);
		if (userRes.getFirst().isPresent() && delete) {
			userRepo.deleteById(userRes.getFirst().get().getId());
			userRepo.flush();
		}
		assertEquals(userRes.getFirst().isPresent(), good, email + " " + password);
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
			return userRes.getFirst().get().getId();
		}
		return null;
	}
	
	@Test(dataProvider = "register")
	public void registerUserTest(String lastName, String firstName, String middleName, 
			String phoneNumber, String address, String password,
			String lastNameRes, String firstNameRes, String middleNameRes, 
			String phoneNumberRes, String addressRes, String passwordRes, boolean good) {
		good = good && password != null && !password.isEmpty();
		
		// all ok
		registerUserTestEmail(null, lastName, firstName, middleName, phoneNumber, address, "100@100.com", password, lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, "100@100.com", passwordRes, good);
		// spaces
		registerUserTestEmail(null, lastName, firstName, middleName, phoneNumber, address, "   100@100.com\t", password, lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, "100@100.com", passwordRes, good);
		// invalid
		registerUserTestEmail(null, lastName, firstName, middleName, phoneNumber, address, "100100.com", password, lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, null, passwordRes, false);
		registerUserTestEmail(null, lastName, firstName, middleName, phoneNumber, address, "", password, lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, null, passwordRes, false);
		registerUserTestEmail(null, lastName, firstName, middleName, phoneNumber, address, null, password, lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, null, passwordRes, false);
		// already used
		registerUserTestEmail(null, lastName, firstName, middleName, phoneNumber, address, "1@1.com", password, lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, null, passwordRes, false);
		// check all deleted
		var users = userRepo.findAll(Sort.by(Order.asc("email"))).stream().map(User::getEmail).toArray();
		assertTrue(Arrays.deepEquals(users,
					new String[] {"1@1.com", "2@1.com", "3@1.com"}));
	}
	
	private final String INITIAL_EMAIL = "5@5.com";
	private final String INITIAL_PWD = "goodpwd100";
	@Test(dataProvider = "register")
	public void updateUserTest(String lastName, String firstName, String middleName, 
			String phoneNumber, String address, String password,
			String lastNameRes, String firstNameRes, String middleNameRes, 
			String phoneNumberRes, String addressRes, String passwordRes, boolean good) {
		
		assertFalse(userRepo.findByEmailIgnoreCase(INITIAL_EMAIL).isPresent());
		
		final String INITIAL_PWD_HASH = userRepo.getPasswordHash(INITIAL_PWD);
		final int UPDATE_USER_ID = registerUserTestEmail(false, null, "Ivanov", "Ivan", null, null, null, INITIAL_EMAIL, INITIAL_PWD, "Ivanov", "Ivan", null, null, null, INITIAL_EMAIL, INITIAL_PWD_HASH, true);
		assertTrue(userRepo.findById(UPDATE_USER_ID).isPresent());
		
		boolean pwdChanges = password != null && !password.isEmpty();
		passwordRes = pwdChanges ? passwordRes : INITIAL_PWD_HASH;
		// all ok
		registerUserTestEmail(false, UPDATE_USER_ID, lastName, firstName, middleName, phoneNumber, address, "100@100.com", password, lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, "100@100.com", passwordRes, good);
		passwordRes = pwdChanges ? passwordRes : userRepo.findById(UPDATE_USER_ID).get().getPwdHash();
		// login and password simultaneously
		registerUserTestEmail(false, UPDATE_USER_ID, lastName, firstName, middleName, phoneNumber, address, "100@101.com", password + "100", lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, null, null, false);
		registerUserTestEmail(false, UPDATE_USER_ID, lastName, firstName, middleName, phoneNumber, address, "100@101.com", password, lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, "100@101.com", passwordRes, good);
		registerUserTestEmail(false, UPDATE_USER_ID, lastName, firstName, middleName, phoneNumber, address, "100@100.com", password, lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, "100@100.com", passwordRes, good);
		// spaces
		passwordRes = pwdChanges ? passwordRes : userRepo.findById(UPDATE_USER_ID).get().getPwdHash();
		registerUserTestEmail(false, UPDATE_USER_ID, lastName, firstName, middleName, phoneNumber, address, "   100@100.com\t", password, lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, "100@100.com", passwordRes, good);
		// invalid
		registerUserTestEmail(false, UPDATE_USER_ID, lastName, firstName, middleName, phoneNumber, address, "100100.com", password, lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, null, passwordRes, false);
		registerUserTestEmail(false, UPDATE_USER_ID, lastName, firstName, middleName, phoneNumber, address, "", password, lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, null, passwordRes, false);
		registerUserTestEmail(false, UPDATE_USER_ID, lastName, firstName, middleName, phoneNumber, address, null, password, lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, null, passwordRes, false);
		// already used
		passwordRes = pwdChanges ? passwordRes : userRepo.findById(UPDATE_USER_ID).get().getPwdHash();
		registerUserTestEmail(false, UPDATE_USER_ID, lastName, firstName, middleName, phoneNumber, address, "100@100.com", password, lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, "100@100.com", passwordRes, good);
		
		var users = userRepo.findAll(Sort.by(Order.asc("email"))).stream().map(User::getEmail).toArray();
		var expected = new String[] {"1@1.com", "2@1.com", "3@1.com", userRepo.findById(UPDATE_USER_ID).get().getEmail()};
		Arrays.sort(expected);
		var eq = Arrays.deepEquals(users, expected);
		assertTrue(eq, Arrays.stream(expected).reduce("", (s1, s2) -> s1 + "," + s2));
		
		userRepo.delete(userRepo.findById(UPDATE_USER_ID).get());
		userRepo.flush();
		assertFalse(userRepo.findById(UPDATE_USER_ID).isPresent());
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
		
		// check deleted
		assertTrue(userRepo.saveAndFlush(userRepo.findById(2).get().setDeleted(true)).isDeleted());
		userRepo.approveEmail("smth2");
		assertFalse(userRepo.orderingAvailable(2));
		assertFalse(userRepo.saveAndFlush(userRepo.findById(2).get().setDeleted(false)).isDeleted());
		
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
