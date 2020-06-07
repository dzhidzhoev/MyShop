package com.myshop.pages;

import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

@Component
@RegisterPath(paths = {"/item"})
public class ItemPage extends GeneralPage {
	public ItemPage() {}
	
	public ItemPage(WebDriver driver) {
		super(driver);
	}
}
