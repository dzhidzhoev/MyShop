package com.myshop.controller;

import static org.assertj.core.api.Assertions.fail;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.net.MalformedURLException;
import java.util.ArrayList;

import org.assertj.core.util.Lists;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.myshop.MyShopApplication;
import com.myshop.UserRepositoryTests;
import com.myshop.UserRepositoryTests.Suite;
import com.myshop.pages.GeneralPage;
import com.myshop.pages.RegisterPage;
import com.myshop.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = MyShopApplication.class)
@TestPropertySource(value = "classpath:test.properties", properties = {"debug.dont_send_verification_email = true"})
public class UserControllerIntegrationTest extends AbstractTestNGSpringContextTests {
	
	WebDriver driver;
	
	@Autowired
	private UserRepository userRepo;
	
	private static final String INITIAL_NAME = "Ivanov";
	private final String INITIAL_EMAIL = "5@5.com";
	private final String INITIAL_PWD = "goodpwd100";
	@DataProvider(name = "registerUI")
	public Object[][] generateRegisterTestData() {
		var names = Lists.list(
				new Suite<>(null, null, false),
				new Suite<>("", "", false),
				new Suite<>("Иванов", "Иванов", true),
				new Suite<>("    Ivanov  ", INITIAL_NAME, true));
		var firstNames = names;
		var lastNames = names;
		var middleNames = Lists.list(
				new Suite<>(null, null, true),
				new Suite<>("", "", true),
				new Suite<>("Иванов", "Иванов", true),
				new Suite<>("    Ivanov  ", INITIAL_NAME, true));
		var valNull = Lists.list(
				new Suite<>(null, null, true),
				new Suite<>("val", "val", true));
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
	
	@BeforeSuite
	public void init() {
		driver = new HtmlUnitDriver();
	}
	
	@Test(dataProvider = "registerUI")
	public void testRegister(String lastName, String firstName, String middleName, 
			String phoneNumber, String address,
			String lastNameRes, String firstNameRes, String middleNameRes, 
			String phoneNumberRes, String addressRes, boolean good) {
		final String INITIAL_PWD_HASH = userRepo.getPasswordHash(INITIAL_PWD);
		
		// all ok
		registerUserTestEmail(driver, true, null, lastName, firstName, middleName, phoneNumber, address, INITIAL_EMAIL, INITIAL_PWD, lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, INITIAL_EMAIL, INITIAL_PWD_HASH, good);
	}
	
	@Test
	public void testRegisterEmail() {
		final String INITIAL_PWD_HASH = userRepo.getPasswordHash(INITIAL_PWD);
		// all ok
		registerUserTestEmail(driver, true, null, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD_HASH, true);
		// spaces
		registerUserTestEmail(driver, true, null, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, "  " + INITIAL_EMAIL + "\t", INITIAL_PWD, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD_HASH, true);
		// invalid
		registerUserTestEmail(driver, true, null, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, "100100.com", INITIAL_PWD, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, null, INITIAL_PWD_HASH, false);
		registerUserTestEmail(driver, true, null, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, "", INITIAL_PWD, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, null, INITIAL_PWD_HASH, false);
		registerUserTestEmail(driver, true, null, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, null, INITIAL_PWD, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, null, INITIAL_PWD_HASH, false);
		// already used
		registerUserTestEmail(driver, true, null, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, "1@1.com", INITIAL_PWD, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, null, INITIAL_PWD_HASH, false);
	}
	
	@Test
	public void testRegisterPassword() {
		final String INITIAL_PWD_HASH = userRepo.getPasswordHash(INITIAL_PWD);
		// empty first & second
		registerUserTestEmailPwd(driver, true, null, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, "", INITIAL_PWD, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD_HASH, false);
		registerUserTestEmailPwd(driver, true, null, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD, "", INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD_HASH, false);
		registerUserTestEmailPwd(driver, true, null, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, "", "", INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, userRepo.getPasswordHash(""), false);
		// invalid
		registerUserTestEmailPwd(driver, true, null, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, "small", "small", INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, userRepo.getPasswordHash("small"), false);
		// not equal
		registerUserTestEmailPwd(driver, true, null, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD, INITIAL_PWD + " ", INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD_HASH, false);
		// good
		registerUserTestEmailPwd(driver, true, null, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD, INITIAL_PWD, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_NAME, INITIAL_EMAIL, INITIAL_PWD_HASH, true);
	}
	
	private Integer registerUserTestEmail(WebDriver driver, boolean delete, Integer id, String lastName, String firstName, String middleName, 
			String phoneNumber, String address, String email, String password,
			String lastNameRes, String firstNameRes, String middleNameRes, 
			String phoneNumberRes, String addressRes, String emailRes, String passwordRes, boolean good) {
		return registerUserTestEmailPwd(driver, delete, id, lastName, firstName, middleName, phoneNumber, address, email, password, password, lastNameRes, firstNameRes, middleNameRes, phoneNumberRes, addressRes, emailRes, passwordRes, good);
	}
	
	private Integer registerUserTestEmailPwd(WebDriver driver, boolean delete, Integer id, String lastName, String firstName, String middleName, 
			String phoneNumber, String address, String email, String password, String password2,
			String lastNameRes, String firstNameRes, String middleNameRes, 
			String phoneNumberRes, String addressRes, String emailRes, String passwordRes, boolean good) {
		
		boolean pwdChanges = id != null && !(INITIAL_PWD.equals(password) || password == null || password.isEmpty());
		boolean emailChanges = id != null && !INITIAL_EMAIL.equals(email);
		good = good && !(pwdChanges && emailChanges);
		
		RegisterPage regPage = RegisterPage.to(driver);
		GeneralPage result = null;
		try {
			result = regPage.doRegister(lastName, firstName, middleName, phoneNumber, address, email, password, password2);
		} catch (MalformedURLException e) {
			fail(e.toString());
		}
		if (good) {
			assertFalse(result instanceof RegisterPage);
		} else {
			assertTrue(result instanceof RegisterPage, result.getClass().toString());
			regPage = (RegisterPage) result;
			assertTrue(!regPage.getErrorMessage().trim().isEmpty(), regPage.getErrorMessage());
		}
		
		var userRes = userRepo.findByEmailIgnoreCase(emailRes);
		if (userRes.isPresent() && delete) {
			userRepo.deleteById(userRes.get().getId());
			userRepo.flush();
		}
		assertEquals(userRes.isPresent(), good, email);
		if (userRes.isPresent()) {
			var user = userRes.get();
			assertTrue(user.getEmailToken() != null);
			assertEquals(user.getLastName(), lastNameRes == null ? "" : lastNameRes);
			assertEquals(user.getFirstName(), firstNameRes == null ? "" : firstNameRes);
			assertEquals(user.getMiddleName(), middleNameRes == null ? "" : middleNameRes);
			assertEquals(user.getPhone(), phoneNumberRes == null ? "" : phoneNumberRes);
			assertEquals(user.getAddress(), addressRes == null ? "" : addressRes);
			assertEquals(user.getEmail(), emailRes);
			assertEquals(user.getPwdHash(), passwordRes);
			return user.getId();
		}
		return null;
	}
}
