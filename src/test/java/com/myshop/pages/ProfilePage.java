package com.myshop.pages;

import java.net.MalformedURLException;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.stereotype.Component;

@Component
@RegisterPath(paths = {"/profile"})
public class ProfilePage extends UserDataPage {

	@FindBy(css = "a[href=\"/logout\"]")
	private WebElement logoutButton;
	
	public GeneralPage logOut() throws MalformedURLException {
		logoutButton.click();
		return ppd.openPage();
	}

	public static ProfilePage to(PagePathsDispatcher ppd) throws MalformedURLException {
		ppd.getDriver().get("http://localhost:8080/profile");
		return (ProfilePage) ppd.openPage();
	}
	
	public GeneralPage addItem(int categoryId) throws MalformedURLException {
		ppd.getJavascriptExecutor().executeScript(
				"$('#add-item-dialog-button').click();"
				+ "$('select[name=categoryId]').val(arguments[0]);"
				+ "$('#add-item-button').click();", categoryId);

		return ppd.openPage();
	}
}
