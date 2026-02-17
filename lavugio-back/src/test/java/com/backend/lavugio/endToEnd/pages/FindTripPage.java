package com.backend.lavugio.endToEnd.pages;

import org.openqa.selenium.By;
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

    // Navbar element to verify page loaded
    @FindBy(xpath = "//app-navbar")
    WebElement navbar;

    public FindTripPage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(this.driver, this);
        waitForPageToLoad();
    }

    private void waitForPageToLoad(){
        // Wait for navbar to appear (indicates logged-in page loaded)
        new WebDriverWait(this.driver, Duration.ofSeconds(15)).until(
                ExpectedConditions.or(
                        ExpectedConditions.visibilityOfElementLocated(By.xpath("//app-navbar")),
                        ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Logout')]"))
                )
        );
    }

    public void clickHistoryBtn(){
        new WebDriverWait(this.driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(historyBtn)).click();
    }

}
