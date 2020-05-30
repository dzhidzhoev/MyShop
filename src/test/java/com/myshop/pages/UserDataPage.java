package com.myshop.pages;

import java.net.MalformedURLException;

import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.FindBy;

public class UserDataPage extends GeneralPage {

	@FindBy(css = "input[name=userId]")
	protected WebElement userId;
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
	
	public GeneralPage doUpdate(Integer id, String lastName, String firstName, String middleName, String phoneNumber, String address, String email,
			String password, String password2) throws MalformedURLException {
		((JavascriptExecutor) driver).executeScript("arguments[0].value = " + id + ";", userId);
		if (!userId.getAttribute("value").equals(id.toString())) {
			throw new JavascriptException("failed to set userId value");
		}
		return doRegister(lastName, firstName, middleName, phoneNumber, address, email, password, password2);
	}

	public GeneralPage doRegister(String lastName, String firstName, String middleName, String phoneNumber, String address, String email,
			String password, String password2) throws MalformedURLException {
		this.lastName.clear();
		this.lastName.sendKeys(lastName == null ? "" : lastName);
		this.firstName.clear();
		this.firstName.sendKeys(firstName == null ? "" : firstName);
		this.middleName.clear();
		this.middleName.sendKeys(middleName == null ? "" : middleName);
		this.phoneNumber.clear();
		this.phoneNumber.sendKeys(phoneNumber == null ? "" : phoneNumber);
		this.address.clear();
		this.address.sendKeys(address == null ? "" : address);
		this.email.clear();
		this.email.sendKeys(email == null ? "" : email);
		return doUpdatePassword(password, password2);
	}

	public GeneralPage doUpdatePassword(String password, String password2) throws MalformedURLException {
		this.password.clear();
		this.password.sendKeys(password == null ? "" : password);
		this.password2.clear();
		this.password2.sendKeys(password2 == null ? "" : password2);
		if (driver instanceof HtmlUnitDriver) {
			((HtmlUnitDriver) driver).setJavascriptEnabled(false);
		}
		submit.submit();
		if (driver instanceof HtmlUnitDriver) {
			((HtmlUnitDriver) driver).setJavascriptEnabled(true);
		}
		return PagePathsDispatcher.getInstance().openPage(driver);
	}
}
