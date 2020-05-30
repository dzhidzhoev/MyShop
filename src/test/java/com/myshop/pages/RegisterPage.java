package com.myshop.pages;

import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

@Component
@RegisterPath(paths = "/register")
public class RegisterPage extends UserDataPage {
	
	public RegisterPage() {}
	
	public RegisterPage(WebDriver driver) {
		super(driver);
	}
	
	public static UserDataPage to(WebDriver driver) {
		driver.get("http://localhost:8080/register");
		return new RegisterPage(driver);
	}
}
