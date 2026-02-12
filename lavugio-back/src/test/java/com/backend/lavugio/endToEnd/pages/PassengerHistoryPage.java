package com.backend.lavugio.endToEnd.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class PassengerHistoryPage {

    WebDriver driver;

    @FindBy(xpath = "(//app-table//app-row)[1]")
    public WebElement cancelledRideItem;

    @FindBy(xpath = "(//app-table//app-row)[2]")
    public WebElement reviewableRideItem;

    @FindBy(xpath = "(//app-table//app-row)[3]")
    public WebElement moreThan3DaysPassedRideItem;

    public PassengerHistoryPage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(this.driver, this);
    }

    public void clickElement(WebElement elementToBeClicked){
        new WebDriverWait(this.driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(elementToBeClicked))
                .click();
    }
}