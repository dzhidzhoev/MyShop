package com.myshop.pages;

import java.util.HashSet;
import java.util.Set;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class GeneralPage {
	
	protected WebDriver driver = null;
	protected Wait<WebDriver> wait = null;

	public GeneralPage() {}
	
	public GeneralPage(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, 30);
		PageFactory.initElements(driver, this);
	}
	
	public static Set<String> getPaths() {
		return new HashSet<>();
	}
}
