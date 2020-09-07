package com.myshop.pages;

import java.net.MalformedURLException;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.data.util.Pair;

import com.myshop.model.TypeEnum;

public class EditItemTraitPage extends EditItemPage {
	private WebElement idEl;
	@FindBy(css = "#submit-traits-button")
	WebElement submitTraitsButton;

	private WebElement detectTraitElement(int traitId) {
		idEl = ppd.getDriver().findElement(By.cssSelector("div > .traitID[value=\"" + traitId + "\"]"));
		return (WebElement) ppd.getJavascriptExecutor().executeScript("return arguments[0].parentNode;", idEl);
	}
	
	private Optional<WebElement> traitValueElement(WebElement traitItemEl) {
		var traitType = TypeEnum.valueOf(traitItemEl.findElement(By.cssSelector(".traitType")).getAttribute("value"));
		switch (traitType) {
		case StringType:
			return Optional.of(traitItemEl.findElement(By.cssSelector(".value")));
		case IntType:
			return Optional.of(traitItemEl.findElement(By.cssSelector(".valueInt")));
		case EnumType:
			return Optional.of(traitItemEl.findElement(By.cssSelector(".valueEnum:checked")));
		default:
			return null;
		}
	}
	
	public String getTraitValue(int traitId) {
		var traitItemEl = detectTraitElement(traitId);
		return traitValueElement(traitItemEl).map(x -> x.getAttribute("value")).orElse("");
	}

	@SafeVarargs
	public final GeneralPage updateTraits(Pair<Integer, String>... traits) throws MalformedURLException {
		for (Pair<Integer, String> arg: traits) {
			int id = arg.getFirst();
			String val = arg.getSecond();
			WebElement traitItemEl = detectTraitElement(id);
			var traitValEl = traitValueElement(traitItemEl).get();
			var inputType = traitValEl.getAttribute("type");
			switch (inputType) {
			case "text":
			case "number":
				traitValEl.clear();
				traitValEl.sendKeys(val);
				break;
			case "radio":
				traitItemEl.findElements(By.cssSelector(".valueEnum:checked")).forEach(el -> el.click());
				traitItemEl.findElement(By.xpath("//*[contains(text(),'" + val + "')]")).click();
				break;
			default:
				break;
			}
		}
		submitTraitsButton.click();
		return ppd.openPage();
	}
}
