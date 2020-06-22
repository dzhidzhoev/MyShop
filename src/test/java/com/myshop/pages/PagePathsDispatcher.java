package com.myshop.pages;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cglib.beans.BeanGenerator;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

public class PagePathsDispatcher {
	
	private HtmlUnitDriver driver;
	private JavascriptExecutor js;
	private int serverPort;
	
	@SuppressWarnings("serial")
	static class InvalidPageClassException extends RuntimeException {
	}
	
	private HashMap<String, Class<? extends GeneralPage>> holder = new HashMap<>(); 
	
	public PagePathsDispatcher(HtmlUnitDriver driver, JavascriptExecutor js, int port) {
		this.driver = driver;
		this.js = js;
		this.serverPort = port;
		
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(RegisterPath.class));
		for (BeanDefinition bd: scanner.findCandidateComponents(getClass().getPackageName())) {
			try { 
				Class<? extends GeneralPage> clazz = BeanGenerator.class
						.getClassLoader()
						.loadClass(bd.getBeanClassName())
						.asSubclass(GeneralPage.class);
				var rpa = clazz.getAnnotation(RegisterPath.class);
				for (String path: rpa.paths()) {
					registerPath(path, clazz);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new InvalidPageClassException();
			}
		}
	}
	
	public void registerPath(String path, Class<? extends GeneralPage> pageClass) {
		if (holder.containsKey(path)) {
			throw new InvalidArgumentException("path is already registered");
		}
		holder.put(path, pageClass);
	}
	
	public void unregisterPath(String path) {
		holder.remove(path);
	}
	
	public GeneralPage openPage() throws MalformedURLException {
		var url = new URL(driver.getCurrentUrl());
		var path = url.getPath();
		try {
			GeneralPage page = holder.containsKey(path) ? holder.get(path).getConstructor().newInstance() : new GeneralPage();
			page.setDispatcher(this);
			return page;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			throw new InvalidPageClassException();
		}
	}

	public void setJavascriptEnabled(boolean isEnabled) {
		driver.setJavascriptEnabled(isEnabled);
	}
	
	public WebDriver getDriver() {
		return driver;
	}

	public JavascriptExecutor getJavascriptExecutor() {
		return js;
	}

	public int getServerPort() {
		return serverPort;
	}
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface RegisterPath {
	String[] paths();
}
