package com.myshop.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class GeneralPage {
	
	protected WebDriver driver = null;

	public GeneralPage() {}
	
	public GeneralPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
}
