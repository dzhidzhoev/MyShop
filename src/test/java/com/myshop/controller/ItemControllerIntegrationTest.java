package com.myshop.controller;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.myshop.MyShopApplication;
import com.myshop.pages.EditItemPage;
import com.myshop.pages.EditItemTraitPage;
import com.myshop.pages.ItemPage;
import com.myshop.pages.LoginPage;
import com.myshop.pages.PagePathsDispatcher;
import com.myshop.pages.ProfilePage;
import com.myshop.repository.ItemRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = MyShopApplication.class)
@TestPropertySource(value = "classpath:test.properties")
public class ItemControllerIntegrationTest extends AbstractTestNGSpringContextTests {
	HtmlUnitDriver driver;
	@Autowired ItemRepository itemRepo;
	ProfilePage adminProfile;
	private PagePathsDispatcher ppd;
	@LocalServerPort
	private int port;
	
	@Value("classpath:testimg.png")
	private Resource testImg;
	
	@PostConstruct
	public void init() {
		driver = new HtmlUnitDriver();
		driver.setJavascriptEnabled(true);
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		ppd = new PagePathsDispatcher(driver, driver, port);
	}
	
	private void loginAdmin() throws MalformedURLException {
		adminProfile = (ProfilePage) LoginPage.to(ppd).logIn("1@1.com", "1");
	}
	
	private void logoutAdmin() {
		driver.manage().deleteAllCookies();
	}
	
	@Test
	public void addUpdateItemTest() throws IOException {
		loginAdmin();
		
		assertEquals(itemRepo.findAll().size(), 4);
		
		EditItemPage editPage;
		
		// name empty
		adminProfile = (ProfilePage) ProfilePage.to(ppd);
		editPage = (EditItemPage) adminProfile.addItem(1);
		checkUpdateItem(editPage, "", 1, null, 100, 0, true, "Hello", false, true, null);
		adminProfile = (ProfilePage) ProfilePage.to(ppd);
		editPage = (EditItemPage) adminProfile.addItem(2);
		checkUpdateItem(editPage, "   \t", 1, null, 100, 0, true, "Hello", false, true, null);
		//ok
		adminProfile = (ProfilePage) ProfilePage.to(ppd);
		editPage = (EditItemPage) adminProfile.addItem(1);
		checkUpdateItem(editPage, "new item", 1, null, 100, 0, false, "description", true, false, null);
		// change image
		byte[] testImgContent = FileUtils.readFileToByteArray(testImg.getFile());
		Consumer<byte[]> imageChecker = img -> assertEquals(img, testImgContent);
		checkUpdateItem(editPage, "new item", 1, testImg.getFile().getPath(), 100, 0, false, "description", true, false, imageChecker);
		// not changing image
		checkUpdateItem(editPage, "new item", 1, null, 100, 0, false, "description", true, true, imageChecker);
		
		logoutAdmin();
	}
	
	private void checkUpdateItem(EditItemPage editPage, String name, int categoryId, String imagePath, int count, int price, boolean active, String desc, boolean good, boolean delete, Consumer<byte[]> imageChecker) throws MalformedURLException {
		var res = editPage.updateItem(name, categoryId, count, price, active, desc, Optional.ofNullable(imagePath));
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
				if (imageChecker != null) {
					imageChecker.accept(itemRepo.getImageById(item.getId()));
				}
			}
			if (delete) {
				itemRepo.delete(item);
				itemRepo.flush();
				assertEquals(itemRepo.findById(editPage.getId()).isPresent(), false);
			}
		}
	}
	
	@Test
	public void itemTraitTest() throws MalformedURLException {
		loginAdmin();
		
		EditItemTraitPage pg;
		// integer
		pg = (EditItemTraitPage) ((EditItemPage) ItemPage.to(ppd, 1).edit()).editTraits();
		assertEquals(pg.getTraitValue(3), "50");
		pg = ((EditItemTraitPage) ((EditItemPage) pg.updateTraits(Pair.of(3, "60"))).editTraits());
		assertEquals(pg.getTraitValue(3), "60");
		pg = ((EditItemTraitPage) ((EditItemPage) pg.updateTraits(Pair.of(3, "50"))).editTraits());
		assertEquals(pg.getTraitValue(3), "50");
		// string
		pg = (EditItemTraitPage) ((EditItemPage) ItemPage.to(ppd, 1).edit()).editTraits();
		assertEquals(pg.getTraitValue(1), "Me");
		pg = ((EditItemTraitPage) ((EditItemPage) pg.updateTraits(Pair.of(1, "Me2"))).editTraits());
		assertEquals(pg.getTraitValue(1), "Me2");
		pg = ((EditItemTraitPage) ((EditItemPage) pg.updateTraits(Pair.of(1, "Me"))).editTraits());
		assertEquals(pg.getTraitValue(1), "Me");
		
		// enum
		pg = (EditItemTraitPage) ((EditItemPage) ItemPage.to(ppd, 3).edit()).editTraits();
		assertEquals(pg.getTraitValue(6), "Bluray");
		pg = ((EditItemTraitPage) ((EditItemPage) pg.updateTraits(Pair.of(6, "DVD"))).editTraits());
		assertEquals(pg.getTraitValue(6), "DVD");
		pg = ((EditItemTraitPage) ((EditItemPage) pg.updateTraits(Pair.of(6, "Bluray"))).editTraits());
		assertEquals(pg.getTraitValue(6), "Bluray");
	}
}
