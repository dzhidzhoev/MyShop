package com.myshop.pages;

import java.net.MalformedURLException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.stereotype.Component;

@Component
@RegisterPath(paths = {"/login"})
public class LoginPage extends GeneralPage {
	@FindBy(css = "#username")
	protected WebElement email;
	@FindBy(css = "#password")
	protected WebElement password;
	@FindBy(css = "#login-submit-btn")
	protected WebElement loginButton;
	
	public LoginPage() {}
	
	public LoginPage(WebDriver driver) {
		super(driver);
	}

	public static LoginPage to(WebDriver driver) {
		driver.get("http://localhost:8080/login");
		return new LoginPage(driver);
	}
	
	public GeneralPage logIn(String email, String password) throws MalformedURLException {
		if (email == null) {
			email = "";
		}
		if (password == null) {
			password = "";
		}
		this.email.sendKeys(email);
		this.password.sendKeys(password);
		loginButton.click();
		return PagePathsDispatcher.getInstance().openPage(driver);
	}
}
