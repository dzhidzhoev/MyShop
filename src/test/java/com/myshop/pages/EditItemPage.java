package com.myshop.pages;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Optional;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Component;

@Component
@RegisterPath(paths = {"/admin/item"})
public class EditItemPage extends GeneralPage {
	@FindBy(css = "#name")
	private WebElement name;
	@FindBy(css = "input[name=id]")
	private WebElement id;
	@FindBy(css = "#item-image")
	private WebElement itemImageValue;
	@FindBy(css = "#category\\.id")
	private WebElement categorySelect;
	@FindBy(css = "#count-input")
	private WebElement countInput;
	@FindBy(css = "#price-input")
	private WebElement priceInput;
	@FindBy(css = "input[name=active]")
	private WebElement activeBox;
	@FindBy(css = "#desc-area")
	private WebElement description;
	@FindBy(css = "#update-item-button")
	private WebElement updateButton;
	@FindBy(css = "#edit-traits-button")
	private WebElement editTraitsButton;
	@FindBy(css = "#view-item-link")
	private WebElement viewItemButton;
	@FindBy(css = "#customFile")
	private WebElement itemImageChooser;
	@FindBy(css = "#error-message-text-item")
	private WebElement errorMessage;
	
	public int getId() {
		return Integer.valueOf(id.getAttribute("value"));
	}
	
	public String getErrorMessage() {
		return errorMessage.getText();
	}
	
	public GeneralPage viewItem() throws MalformedURLException {
		viewItemButton.click();
		return ppd.openPage();
	}
	
	public GeneralPage updateItem(String name, int categoryId, int count, int price, boolean active, String desc, Optional<String> imagePath) throws MalformedURLException {
		this.name.clear();
		this.name.sendKeys(name);
		Select selector = new Select(categorySelect);
		selector.selectByValue(Integer.toString(categoryId));
		this.countInput.clear();
		this.countInput.sendKeys(Integer.toString(count));
		this.priceInput.clear();
		this.priceInput.sendKeys(Integer.toString(price));
		if (this.activeBox.isSelected() != active) {
			this.activeBox.click();
		}
		this.description.clear();
		this.description.sendKeys(desc);
		imagePath.ifPresent(img -> itemImageChooser.sendKeys(img));
		updateButton.click();
		return ppd.openPage();
	}
	
	public GeneralPage editTraits() {
		editTraitsButton.click();
		GeneralPage page;
		try {
			page = ppd.setupPage(EditItemTraitPage.class);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			throw new TraitEditingException("unable to instantiate page class!");
		}
		return page;
	}
	
	@SuppressWarnings("serial")
	public static class TraitEditingException extends RuntimeException {
		public TraitEditingException(String msg) {
			super(msg);
		}
	}
}
