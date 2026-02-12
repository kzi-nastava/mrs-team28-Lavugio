package com.backend.lavugio.endToEnd.pages;

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

    @FindBy(id = "map")
    WebElement map;

    public HomePage(WebDriver driver){
        this.driver = driver;
        this.driver.get(PAGE_URL);
        PageFactory.initElements(this.driver, this);
        waitForPageToLoad();
    }

    public void goToLoginPage(){
        loginBtn.click();
    }

    private void waitForPageToLoad(){
        new WebDriverWait(this.driver, Duration.ofSeconds(20)).until(ExpectedConditions.visibilityOf(map));
    }


}
