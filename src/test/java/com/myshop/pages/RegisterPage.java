package com.myshop.pages;

import java.net.MalformedURLException;

import org.springframework.stereotype.Component;

@Component
@RegisterPath(paths = "/register")
public class RegisterPage extends UserDataPage {
	
	public static UserDataPage to(PagePathsDispatcher ppd) throws MalformedURLException {
		ppd.getDriver().get("http://localhost:8080/register");
		return (UserDataPage) ppd.openPage();
	}
}
