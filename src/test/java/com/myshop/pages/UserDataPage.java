package com.myshop.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class UserDataPage extends GeneralPage {

	@FindBy(css = "input[name=lastName]")
	protected WebElement lastName;
	@FindBy(css = "input[name=firstName]")
	protected WebElement firstName;
	@FindBy(css = "input[name=middleName]")
	protected WebElement middleName;
	@FindBy(css = "input[name=phoneNumber]")
	protected WebElement phoneNumber;
	@FindBy(css = "textarea[name=address]")
	protected WebElement address;
	@FindBy(css = "input[name=email]")
	protected WebElement email;
	@FindBy(css = "input[name=password]")
	protected WebElement password;
	@FindBy(css = "input[name=password2]")
	protected WebElement password2;
	@FindBy(css = "input[name=redirectPath]")
	protected WebElement redirectPath;
	@FindBy(css = "#register-or-update-form-submit")
	protected WebElement submit;
	@FindBy(css = "#error-message-text-reg")
	protected WebElement errorMessage;
	
	public UserDataPage() {}
	
	public UserDataPage(WebDriver driver) {
		super(driver);
	}

	public String getErrorMessage() {
		return errorMessage.getText();
	}
}
