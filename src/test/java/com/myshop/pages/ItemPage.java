package com.myshop.pages;

import java.net.MalformedURLException;

import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

@Component
@RegisterPath(paths = {"/item"})
public class ItemPage extends GeneralPage {
	
	public static ItemPage to(PagePathsDispatcher ppd, int id) throws MalformedURLException {
		ppd.getDriver().get("http://localhost:" + ppd.getServerPort() + "/item?id=" + id);
		return (ItemPage) ppd.openPage();
	}
	
	public GeneralPage edit() throws MalformedURLException {
		ppd.getDriver().findElement(By.id("item-edit-link")).click();
		return ppd.openPage();
	}
}
