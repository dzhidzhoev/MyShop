package com.myshop.pages;

import org.openqa.selenium.support.PageFactory;

public class GeneralPage {
	
	protected PagePathsDispatcher ppd = null;

	public void setDispatcher(PagePathsDispatcher ppd) {
		this.ppd = ppd;
		PageFactory.initElements(ppd.getDriver(), this);
	}
}
