package com.myshop.controller;

import static org.assertj.core.api.Assertions.fail;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

import org.apache.commons.lang3.ArrayUtils;
import org.assertj.core.util.IterableUtil;
import org.assertj.core.util.Lists;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.myshop.MyShopApplication;
import com.myshop.UserRepositoryTests;
import com.myshop.UserRepositoryTests.Suite;
import com.myshop.model.User;
import com.myshop.pages.GeneralPage;
import com.myshop.pages.LoginPage;
import com.myshop.pages.ProfilePage;
import com.myshop.pages.RegisterPage;
import com.myshop.pages.UserDataPage;
import com.myshop.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = MyShopApplication.class)
@TestPropertySource(value = "classpath:test.properties", properties = {"debug.dont_send_verification_email = true"})
public class UserControllerIntegrationTest extends AbstractTestNGSpringContextTests {
	
	HtmlUnitDriver driver;
	
	@Autowired
	private UserRepository userRepo;
	
	private static final String INITIAL_NAME = "Ivanov";
	private final String INITIAL_EMAIL = "5@5.com";
	private final String INITIAL_PWD = "goodpwd100";
	@DataProvider(name = "registerUI")
	public Object[][] generateRegisterTestData() {
		var names = Lists.list(
				new Suite<>(null, null, false),
				new Suite<>("    Ivanov  ", INITIAL_NAME, true));
		var firstNames = names;
		var lastNames = names;
		var valNull = Lists.list(
				new Suite<>(null, null, true),
				new Suite<>("val", "val", true));
		var middleNames = valNull;
		var phoneNumber = valNull;
		var address = valNull;
		var before = Lists.list(
				lastNames,
				firstNames,
				middleNames,
				phoneNumber, 
				address
			);
		var resultList = new ArrayList<ArrayList<Object>>();
		UserRepositoryTests.fill(before, resultList);
		Object[][] res = new Object[resultList.size()][];
		for (int i = 0; i < resultList.size(); i++) {
			res[i] = resultList.get(i).toArray();
		}
		return res;
	}
	
	@DataProvider(name = "registerUI+userEmail+changerEmail+isAdmin")
	public Object[][] generateRegisterUserTestData() {
		Object[][] registerUI = generateRegisterTestData();
		ArrayList<Object[]> uci = new ArrayList<Object[]>();
		var emails = Lists.list(INITIAL_EMAIL, "6@6.com");
		for (String userId: emails) {
			for (boolean isAdmin: Lists.list(false, true)) {
				uci.add(new Object[] {userId, isAdmin});
			}
		}
		var oldLen = registerUI.length;
		Iterable<Object[]> it = new Iterable<Object[]>() {
			
			@Override
			public Iterator<Object[]> iterator() {
				return new Iterator<Object[]>() {
					int i = 0, j = 0;
					
					@Override
					public Object[] next() {
						var res = ArrayUtils.addAll(registerUI[i], 
								uci.get(j));
						
						j++;
						if (j >= uci.size()) {
							j = 0;
							i++;
						}
						return res;
					}
					
					@Override
					public boolean hasNext() {
						return i != registerUI.length;
					}
				};
			}
		};
		var res = IterableUtil.toArray(it, Object[].class);
		assertEquals(res.length, oldLen * 4);
		return res;
	}
	
	@BeforeSuite
	public void init() {
		driver = new HtmlUnitDriver();
		driver.setJavascriptEnabled(true);
	}
	
	@Test(dataProvider = "registerUI")
	public void testRegister(String lastName, String firstName, String middleName, 
			String phoneNumber, String address,
			String lastNameRes, String firstNameRes, String middleNameRes, 
			String phoneNumberRes, String addressRes, boolean good) {
		final String INITIAL_PWD_HASH = userRepo.getPasswordHash(INITIAL_PWD);
		
		// all ok
		registerOrUpdateUserTestEmailOnRegister(RegisterPage.to(driver), true, null, null, lastName, firstName, middleName, phoneNumber, address, INITIAL_EMAIL, INITIAL_PWD, lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, INITIAL_EMAIL, INITIAL_PWD_HASH, good);
	}
	
	@Test
	public void testRegisterEmail() {
		final String INITIAL_PWD_HASH = userRepo.getPasswordHash(INITIAL_PWD);
		UserDataPage regPage;
		// all ok
		regPage = RegisterPage.to(driver);
		registerOrUpdateUserTestEmailOnRegister(regPage, true, null, null, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD_HASH, true);
		// spaces
		regPage = RegisterPage.to(driver);
		registerOrUpdateUserTestEmailOnRegister(regPage, true, null, null, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, "  " + INITIAL_EMAIL + "\t", INITIAL_PWD, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD_HASH, true);
		// invalid
		regPage = RegisterPage.to(driver);
		registerOrUpdateUserTestEmailOnRegister(regPage, true, null, null, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, "100100.com", INITIAL_PWD, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, null, INITIAL_PWD_HASH, false);
		regPage = RegisterPage.to(driver);
		registerOrUpdateUserTestEmailOnRegister(regPage, true, null, null, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, "", INITIAL_PWD, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, null, INITIAL_PWD_HASH, false);
		regPage = RegisterPage.to(driver);
		registerOrUpdateUserTestEmailOnRegister(regPage, true, null, null, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, null, INITIAL_PWD, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, null, INITIAL_PWD_HASH, false);
		// already used
		regPage = RegisterPage.to(driver);
		registerOrUpdateUserTestEmailOnRegister(regPage, true, null, null, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, "1@1.com", INITIAL_PWD, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, null, INITIAL_PWD_HASH, false);
	}
	
	@Test
	public void testRegisterPassword() {
		final String INITIAL_PWD_HASH = userRepo.getPasswordHash(INITIAL_PWD);
		UserDataPage regPage;
		// empty first & second
		regPage = RegisterPage.to(driver);
		registerOrUpdateUserTestEmailPwd(regPage, true, null, null, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, "", INITIAL_PWD, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD_HASH, RegisterPage.class, false);
		regPage = RegisterPage.to(driver);
		registerOrUpdateUserTestEmailPwd(regPage, true, null, null, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD, "", INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD_HASH, RegisterPage.class, false);
		regPage = RegisterPage.to(driver);
		registerOrUpdateUserTestEmailPwd(regPage, true, null, null, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, "", "", INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, userRepo.getPasswordHash(""), RegisterPage.class, false);
		// invalid
		regPage = RegisterPage.to(driver);
		registerOrUpdateUserTestEmailPwd(regPage, true, null, null, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, "small", "small", INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, userRepo.getPasswordHash("small"), RegisterPage.class, false);
		// not equal
		regPage = RegisterPage.to(driver);
		registerOrUpdateUserTestEmailPwd(regPage, true, null, null, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD, INITIAL_PWD + " ", INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD_HASH, RegisterPage.class, false);
		// good
		regPage = RegisterPage.to(driver);
		registerOrUpdateUserTestEmailPwd(regPage, true, null, null, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD, INITIAL_PWD, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD_HASH, RegisterPage.class, true);
	}
	
	private Pair<ProfilePage, Integer> beforeUpdate(String email) throws MalformedURLException {
		assertFalse(userRepo.findByEmailIgnoreCase(email).isPresent());
		
		final int UPDATE_USER_ID = userRepo.registerUser(null, "Ivanov", "Ivan", "", "", "", email, INITIAL_PWD).getFirst().get().getId();
		assertTrue(userRepo.findById(UPDATE_USER_ID).isPresent());
		
		driver.manage().deleteAllCookies();
		var pp = (ProfilePage) LoginPage.to(driver).logIn(email, INITIAL_PWD);
		return Pair.of(pp, UPDATE_USER_ID);
	}
	
	private Pair<ProfilePage, Integer> beforeUpdate() throws MalformedURLException {
		return beforeUpdate(INITIAL_EMAIL);
	}
	
	private void afterUpdate(int id) throws MalformedURLException {
		driver.manage().deleteAllCookies();
		userRepo.delete(userRepo.findById(id).get());
		userRepo.flush();
		assertFalse(userRepo.findById(id).isPresent());
	}
	
	@Test
	public void testUpdateNotLoggedIn() throws MalformedURLException {
		var pp = beforeUpdate();
		var profile = pp.getFirst();
		final int UPDATE_USER_ID = pp.getSecond();
		
		var res = profile.doRegister("Ivanov2", "Ivan", "Ivanovich", null, null, INITIAL_EMAIL, INITIAL_PWD, INITIAL_PWD);
		assertTrue(res instanceof ProfilePage);
		profile = (ProfilePage) res;
		driver.manage().deleteAllCookies();
		res = profile.doRegister("Ivanov2", "Ivan", "Ivanovich", null, null, INITIAL_EMAIL, INITIAL_PWD, INITIAL_PWD);
		assertTrue(res instanceof LoginPage);
		
		afterUpdate(UPDATE_USER_ID);
	}
	
	@Test(dataProvider = "registerUI+userEmail+changerEmail+isAdmin")
	public void testUpdate(String lastName, String firstName, String middleName, 
			String phoneNumber, String address,
			String lastNameRes, String firstNameRes, String middleNameRes, 
			String phoneNumberRes, String addressRes, boolean good, 
			String changerEmail, boolean isAdmin) throws MalformedURLException {
		// create users
		var pp = beforeUpdate();
		int userId = pp.getSecond();
		int changerId = userId;
		if (!changerEmail.equals(INITIAL_EMAIL)) {
			pp = beforeUpdate(changerEmail);
			changerId = pp.getSecond();
		}
		userRepo.saveAndFlush(userRepo.findById(changerId).get().setAdmin(isAdmin));
		final String INITIAL_PWD_HASH = userRepo.getPasswordHash(INITIAL_PWD);
		
		good = good && (changerId == userId);
		ProfilePage profile = pp.getFirst();
		registerOrUpdateUserTestEmailPwdWithBadChecker(profile, false, userId, INITIAL_EMAIL, 
				lastName, firstName, middleName, phoneNumber, address, INITIAL_EMAIL, null, null, 
				lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, INITIAL_EMAIL, INITIAL_PWD_HASH, 
				result -> {
					String resClassName = result.getClass().getName();
					assertTrue(resClassName.equals(LoginPage.class.getName()) 
							|| resClassName.equals(ProfilePage.class.getName()), resClassName);
					if (resClassName.equals(ProfilePage.class.getName())) {
						var udp = (UserDataPage) result;
						assertTrue(!udp.getErrorMessage().trim().isEmpty());
					}
				},  good);
		
		// delete users
		afterUpdate(userId);
		if (userId != changerId) {
			afterUpdate(changerId);
		}
	}
	
	@Test()
	public void testUpdateEmail() throws MalformedURLException {
		final String NEW_EMAIL = INITIAL_EMAIL + "smth";
		
		var pp = beforeUpdate();
		int userId = pp.getSecond();
		
		final String INITIAL_PWD_HASH = userRepo.getPasswordHash(INITIAL_PWD);
		UserDataPage profilePage;
		// spaces
		profilePage = ProfilePage.to(driver);
		registerOrUpdateUserTestEmailPwd(profilePage, false, userId, INITIAL_EMAIL, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, "  " + INITIAL_EMAIL + "\t", INITIAL_PWD, INITIAL_PWD, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD_HASH, ProfilePage.class, true);
		// invalid
		profilePage = ProfilePage.to(driver);
		registerOrUpdateUserTestEmailPwd(profilePage, false, userId, INITIAL_EMAIL, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, "100100.com", INITIAL_PWD, INITIAL_PWD, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, null, INITIAL_PWD_HASH, ProfilePage.class, false);
		profilePage = ProfilePage.to(driver);
		registerOrUpdateUserTestEmailPwd(profilePage, false, userId, INITIAL_EMAIL, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, "", INITIAL_PWD, INITIAL_PWD, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, null, INITIAL_PWD_HASH, ProfilePage.class, false);
		profilePage = ProfilePage.to(driver);
		registerOrUpdateUserTestEmailPwd(profilePage, false, userId, INITIAL_EMAIL, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, null, INITIAL_PWD, INITIAL_PWD, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, null, INITIAL_PWD_HASH, ProfilePage.class, false);
		// already used
		profilePage = ProfilePage.to(driver);
		registerOrUpdateUserTestEmailPwd(profilePage, false, userId, INITIAL_EMAIL, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, "1@1.com", INITIAL_PWD, INITIAL_PWD, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, null, INITIAL_PWD_HASH, ProfilePage.class, false);
		// pwd not equal
		profilePage = ProfilePage.to(driver);
		registerOrUpdateUserTestEmailPwd(profilePage, false, userId, INITIAL_EMAIL, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, NEW_EMAIL, INITIAL_PWD, "11", INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, NEW_EMAIL, INITIAL_PWD_HASH, ProfilePage.class, false);
		// empty pwd
		profilePage = ProfilePage.to(driver);
		registerOrUpdateUserTestEmailPwd(profilePage, false, userId, INITIAL_EMAIL, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, NEW_EMAIL, null, null, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, NEW_EMAIL, INITIAL_PWD_HASH, ProfilePage.class, false);
		// all ok
		profilePage = ProfilePage.to(driver);
		registerOrUpdateUserTestEmailPwd(profilePage, false, userId, NEW_EMAIL, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL + "smth2", INITIAL_PWD, INITIAL_PWD, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL + "smth2", INITIAL_PWD_HASH, ProfilePage.class, true);
	
		afterUpdate(userId);
	}

	@Test
	public void testUpdatePassword() throws MalformedURLException {
		var pp = beforeUpdate();
		int userId = pp.getSecond();
		
		final String INITIAL_PWD_HASH = userRepo.getPasswordHash(INITIAL_PWD);
		UserDataPage profilePage;
		// empty first | second
		profilePage = ProfilePage.to(driver);
		registerOrUpdateUserTestEmailPwd(profilePage, false, userId, INITIAL_EMAIL, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, "", INITIAL_PWD, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD_HASH, ProfilePage.class, false);
		profilePage = ProfilePage.to(driver);
		registerOrUpdateUserTestEmailPwd(profilePage, false, userId, INITIAL_EMAIL, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD, "", INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD_HASH, ProfilePage.class, false);
		// invalid
		profilePage = ProfilePage.to(driver);
		registerOrUpdateUserTestEmailPwd(profilePage, false, userId, INITIAL_EMAIL, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, "small", "small", INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, userRepo.getPasswordHash("small"), ProfilePage.class, false);
		// not equal
		profilePage = ProfilePage.to(driver);
		registerOrUpdateUserTestEmailPwd(profilePage, false, userId, INITIAL_EMAIL, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD, INITIAL_PWD + "_", INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD_HASH, ProfilePage.class, false);
		// both email & password
		profilePage = ProfilePage.to(driver);
		registerOrUpdateUserTestEmailPwd(profilePage, false, userId, INITIAL_EMAIL, INITIAL_NAME + "_", INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL + "smth", "newgoodpwd1234", "newgoodpwd1234", INITIAL_NAME + "_", INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL + "smth", INITIAL_PWD_HASH, ProfilePage.class, false);
		// empty first & second
		profilePage = ProfilePage.to(driver);
		registerOrUpdateUserTestEmailPwd(profilePage, false, userId, INITIAL_EMAIL, INITIAL_NAME + "_", INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, "", "", INITIAL_NAME + "_", INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD_HASH, ProfilePage.class, true);
		// good
		profilePage = ProfilePage.to(driver);
		registerOrUpdateUserTestEmailPwd(profilePage, false, userId, INITIAL_EMAIL, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD + "smth", INITIAL_PWD + "smth", INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, userRepo.getPasswordHash(INITIAL_PWD + "smth"), ProfilePage.class, true);
		
		afterUpdate(userId);
	}
	
	private Integer registerOrUpdateUserTestEmailOnRegister(UserDataPage regPage, boolean delete, Integer id, String initialEmail,
			String lastName, String firstName, String middleName, 
			String phoneNumber, String address, String email, String password,
			String lastNameRes, String firstNameRes, String middleNameRes, 
			String phoneNumberRes, String addressRes, String emailRes, String passwordRes, boolean good) {
		return registerOrUpdateUserTestEmailPwd(regPage, delete, id, initialEmail, lastName, firstName, middleName, phoneNumber, address, email, password, password,
				lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, emailRes, passwordRes,
				RegisterPage.class, good);
	}
	
	private Integer registerOrUpdateUserTestEmailPwdWithBadChecker(UserDataPage userDataPage, boolean delete, Integer id, String initialEmail, String lastName, String firstName, String middleName, 
			String phoneNumber, String address, String email, String password, String password2,
			String lastNameRes, String firstNameRes, String middleNameRes, 
			String phoneNumberRes, String addressRes, String emailRes, String passwordRes, Consumer<GeneralPage> badChecker,  boolean good) {
		boolean pwdChanges = id != null && !(INITIAL_PWD.equals(password) || password == null || password.isEmpty());
		boolean emailChanges = id != null && !initialEmail.equals(email);
		good = good && !(pwdChanges && emailChanges);
		
		GeneralPage result = null;
		User oldUser = null;
		try {
			if (id != null) {
				oldUser = userRepo.findById(id).get().clone();
				result = userDataPage.doUpdate(id, lastName, firstName, middleName, phoneNumber, address, email, password, password2);
			} else {
				result = userDataPage.doRegister(lastName, firstName, middleName, phoneNumber, address, email, password, password2);
			}
		} catch (MalformedURLException e) {
			fail(e.toString());
		}
		if (good) {
			assertTrue(result instanceof ProfilePage, result.getClass().getName());
		} else {
			badChecker.accept(result);
		}
		
		var userRes = userRepo.findByEmailIgnoreCase(emailRes);
		if (userRes.isPresent() && delete) {
			userRepo.deleteById(userRes.get().getId());
			userRepo.flush();
		}
		if (good || id == null) {
			assertEquals(userRes.isPresent(), good, email);
		}
		if (userRes.isPresent()) {
			var user = userRes.get();
			if (id == null || good) {
				assertUserData(lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, emailRes, passwordRes,
						user);
			} else {
				assertUserData(oldUser.getLastName(), oldUser.getFirstName(), oldUser.getMiddleName(), 
						oldUser.getPhone(), oldUser.getAddress(), oldUser.getEmail(), oldUser.getPwdHash(), user);
			}
			return user.getId();
		}
		return null;
	}
	
	private Integer registerOrUpdateUserTestEmailPwd(UserDataPage userDataPage, boolean delete, Integer id, String initialEmail, String lastName, String firstName, String middleName, 
			String phoneNumber, String address, String email, String password, String password2,
			String lastNameRes, String firstNameRes, String middleNameRes, 
			String phoneNumberRes, String addressRes, String emailRes, String passwordRes, Class<? extends GeneralPage> badPage, boolean good) {
		return registerOrUpdateUserTestEmailPwdWithBadChecker(userDataPage, delete, id, initialEmail, lastName, firstName, middleName, phoneNumber, address, email, password, password2, lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, emailRes, passwordRes,  
				(result) -> {
					assertTrue(result.getClass().getName().equals(badPage.getName()), result.getClass().toString());
					if (result instanceof UserDataPage) {
						var udp = (UserDataPage) result;
						assertTrue(!udp.getErrorMessage().trim().isEmpty(), udp.getErrorMessage());
					}
				}, good);
	}

	private void assertUserData(String lastNameRes, String firstNameRes, String middleNameRes, String phoneNumberRes,
			String addressRes, String emailRes, String passwordRes, User user) {
		assertTrue(user.getEmailToken() != null);
		assertEquals(user.getLastName(), lastNameRes == null ? "" : lastNameRes);
		assertEquals(user.getFirstName(), firstNameRes == null ? "" : firstNameRes);
		assertEquals(user.getMiddleName(), middleNameRes == null ? "" : middleNameRes);
		assertEquals(user.getPhone(), phoneNumberRes == null ? "" : phoneNumberRes);
		assertEquals(user.getAddress(), addressRes == null ? "" : addressRes);
		assertEquals(user.getEmail(), emailRes);
		assertEquals(user.getPwdHash(), passwordRes);
	}
}
