package com.myshop.pages;

import java.net.MalformedURLException;

import org.openqa.selenium.WebDriver;
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
	@FindBy(css = "#view-item-link")
	private WebElement viewItemButton;
	@FindBy(css = "#error-message-text-item")
	private WebElement errorMessage;
	
	public EditItemPage() {}
	
	public EditItemPage(WebDriver driver) {
		super(driver);
	}
	
	public int getId() {
		return Integer.valueOf(id.getAttribute("value"));
	}
	
	public String getErrorMessage() {
		return errorMessage.getText();
	}
	
	public GeneralPage viewItem() throws MalformedURLException {
		viewItemButton.click();
		return PagePathsDispatcher.getInstance().openPage(driver);
	}
	
	public GeneralPage updateItem(String name, int categoryId, int count, int price, boolean active, String desc) throws MalformedURLException {
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
		updateButton.click();
		return PagePathsDispatcher.getInstance().openPage(driver);
	}
}
