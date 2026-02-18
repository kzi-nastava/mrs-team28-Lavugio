package com.backend.lavugio.endToEnd.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HomePage {

    private WebDriver driver;
    private static String PAGE_URL="http://localhost:4200";

    @FindBy(xpath = "//span[text() = 'Login here']")
    WebElement loginBtn;

    // Wait for Login here text to be visible (guest page) or Logout button (logged in)
    @FindBy(xpath = "//span[contains(text(),'Login here')] | //button[contains(text(),'Logout')]")
    WebElement pageLoadIndicator;

    public HomePage(WebDriver driver){
        this.driver = driver;
        this.driver.get(PAGE_URL);
        PageFactory.initElements(this.driver, this);
        waitForPageToLoad();
    }

    public void goToLoginPage(){
        new WebDriverWait(this.driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(loginBtn)).click();
    }

    private void waitForPageToLoad(){
        // Wait for either "Login here" (guest) or "Logout" (logged in) to appear
        new WebDriverWait(this.driver, Duration.ofSeconds(20)).until(
                ExpectedConditions.or(
                        ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'Login here')]")),
                        ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Logout')]")),
                        ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(),'Tired')]"))
                )
        );
    }

}
