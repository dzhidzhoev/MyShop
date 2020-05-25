package com.myshop.pages;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.WebDriver;

public class RegisterPage extends UserDataPage {
	
	private RegisterPage(WebDriver driver) {
		super(driver);
	}
	
	public static RegisterPage to(WebDriver driver) {
		driver.get("http://localhost:8080/register");
		return new RegisterPage(driver);
	}
	
	public GeneralPage doRegister(String lastName, String firstName, String middleName, String phoneNumber, String address, String email, String password, String password2) throws MalformedURLException {
		this.lastName.sendKeys(lastName == null ? "" : lastName);
		this.firstName.sendKeys(firstName == null ? "" : firstName);
		this.middleName.sendKeys(middleName == null ? "" : middleName);
		this.phoneNumber.sendKeys(phoneNumber == null ? "" : phoneNumber);
		this.address.sendKeys(address == null ? "" : address);
		this.email.sendKeys(email == null ? "" : email);
		this.password.sendKeys(password == null ? "" : password);
		this.password2.sendKeys(password2 == null ? "" : password2);
		submit.click();
		var url = new URL(driver.getCurrentUrl());
		return url.getPath().equals("/register") ? new RegisterPage(driver) : new GeneralPage(driver);
	}
}
