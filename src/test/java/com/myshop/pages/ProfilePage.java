package com.myshop.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProfilePage extends GeneralPage {

	@FindBy(css = "a[href=\"/logout\"]")
	private WebElement logoutButton;
	
	public ProfilePage(WebDriver driver) {
		super(driver);
	}

}
