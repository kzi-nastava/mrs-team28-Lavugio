package com.backend.lavugio.endToEnd.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class FindTripPage {

    WebDriver driver;

    @FindBy(xpath = "//app-navbar//span[text()='History']")
    WebElement historyBtn;

    @FindBy(id = "map")
    WebElement map;

    public FindTripPage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(this.driver, this);
        waitForPageToLoad();
    }

    private void waitForPageToLoad(){
        new WebDriverWait(this.driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(map));
    }

    public void clickHistoryBtn(){
        new WebDriverWait(this.driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(historyBtn)).click();
    }


}
