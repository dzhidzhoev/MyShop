package com.myshop.pages;

import java.net.MalformedURLException;

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

	public static LoginPage to(PagePathsDispatcher ppd) throws MalformedURLException {
		ppd.getDriver().get("http://localhost:" + ppd.getServerPort() + "/login");
		return (LoginPage) ppd.openPage();
	}
	
	public GeneralPage logIn(String email, String password) throws MalformedURLException {
		if (email == null) {
			email = "";
		}
		if (password == null) {
			password = "";
		}
		this.email.clear();
		this.email.sendKeys(email);
		this.password.clear();
		this.password.sendKeys(password);
		loginButton.submit();
		return ppd.openPage();
	}
}
