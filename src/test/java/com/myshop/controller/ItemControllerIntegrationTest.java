package com.myshop.controller;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.net.MalformedURLException;

import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.myshop.MyShopApplication;
import com.myshop.pages.EditItemPage;
import com.myshop.pages.LoginPage;
import com.myshop.pages.ProfilePage;
import com.myshop.repository.ItemRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = MyShopApplication.class)
@TestPropertySource(value = "classpath:test.properties")
public class ItemControllerIntegrationTest extends AbstractTestNGSpringContextTests {
	HtmlUnitDriver driver;
	@Autowired ItemRepository itemRepo;
	ProfilePage adminProfile;
	
	@BeforeSuite
	public void init() {
		driver = new HtmlUnitDriver();
		driver.setJavascriptEnabled(true);
	}
	
	private void loginAdmin() throws MalformedURLException {
		adminProfile = (ProfilePage) LoginPage.to(driver).logIn("1@1.com", "1");
	}
	
	private void logoutAdmin() {
		driver.manage().deleteAllCookies();
	}
	
	@Test
	public void addUpdateItemTest() throws MalformedURLException {
		loginAdmin();
		
		assertEquals(itemRepo.findAll().size(), 4);
		
		EditItemPage editPage;
		
		// name empty
		adminProfile = (ProfilePage) ProfilePage.to(driver);
		editPage = (EditItemPage) adminProfile.addItem(1);
		checkUpdateItem(editPage, "", 1, 100, 0, true, "Hello", false);
		adminProfile = (ProfilePage) ProfilePage.to(driver);
		editPage = (EditItemPage) adminProfile.addItem(2);
		checkUpdateItem(editPage, "   \t", 1, 100, 0, true, "Hello", false);
		//ok
		adminProfile = (ProfilePage) ProfilePage.to(driver);
		editPage = (EditItemPage) adminProfile.addItem(1);
		checkUpdateItem(editPage, "new item", 1, 100, 0, false, "description", true);
		
		logoutAdmin();
	}
	
	private void checkUpdateItem(EditItemPage editPage, String name, int categoryId, int count, int price, boolean active, String desc, boolean good) throws MalformedURLException {
		var res = editPage.updateItem(name, categoryId, count, price, active, desc);
		assertTrue(res instanceof EditItemPage || !good);
		if (res instanceof EditItemPage) {
			editPage = (EditItemPage) res;
			assertEquals(editPage.getErrorMessage().trim().isEmpty(), good, "'" + editPage.getErrorMessage() + "'");
			var item = itemRepo.findById(editPage.getId()).get();
			if (good) {
				assertEquals(item.getCategory().getId(), categoryId);
				assertEquals(item.getName(), name);
				assertEquals(item.getPrice(), price);
				assertEquals(item.getCount(), count);
				assertEquals(item.isActive(), active);
				assertEquals(item.getDescription(), desc);
			}
			itemRepo.delete(item);
			itemRepo.flush();
			assertEquals(itemRepo.findById(editPage.getId()).isPresent(), false);
		}
	}
}
