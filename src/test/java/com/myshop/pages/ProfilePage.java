package com.myshop.pages;

import java.net.MalformedURLException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.stereotype.Component;

@Component
@RegisterPath(paths = {"/profile"})
public class ProfilePage extends UserDataPage {

	@FindBy(css = "a[href=\"/logout\"]")
	private WebElement logoutButton;
	
	public ProfilePage() {}
	
	public ProfilePage(WebDriver driver) {
		super(driver);
	}
	
	public GeneralPage logOut() throws MalformedURLException {
		logoutButton.click();
		return PagePathsDispatcher.getInstance().openPage(driver);
	}

	public static UserDataPage to(WebDriver driver) {
		driver.get("http://localhost:8080/profile");
		return new ProfilePage(driver);
	}
}
