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

    @FindBy(xpath = "//app-navbar//span[text()='Order Ride']")
    WebElement orderRideBtn;

    @FindBy(xpath = "//button[text()=' Select favorite route ']")
    WebElement openFavoriteRoutesBtn;

    @FindBy(xpath = "//button[text()=' Select ']")
    WebElement selectFavoriteRouteBtn;

    @FindBy(tagName = "app-destinations-display")
    WebElement destinationsDisplay;

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

    public void clickOrderRideBtn() {
        new WebDriverWait(this.driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(orderRideBtn)).click();
    }

    public void clickOpenFavoriteRoutesBtn() {
        new WebDriverWait(this.driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(openFavoriteRoutesBtn)).click();
    }

    public boolean isNoDestinationsAdded() {
        System.out.println(this.destinationsDisplay.findElement(By.tagName("p")).getText());
        return this.destinationsDisplay.findElement(By.tagName("p")).getText().equals("No destinations added yet");
    }

    public boolean selectFavoriteRoute(String favoriteRouteName) {
        WebElement firstFavoriteRoute = this.destinationsDisplay.findElement(By.xpath("//p[contains(text(),'" + favoriteRouteName + "')]"));
        if (firstFavoriteRoute != null) {
            firstFavoriteRoute.click();
            return true;
        }
        return false;
    }

    public boolean isSelectFavoriteRouteBtnEnabled() {
        try {
            new WebDriverWait(this.driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(selectFavoriteRouteBtn));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean clickSelectFavoriteRouteBtn() {
        try {
            new WebDriverWait(this.driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(selectFavoriteRouteBtn)).click();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDestinationAdded(String destination) {
        try {
            new WebDriverWait(this.driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(destinationsDisplay.findElement(By.xpath("//p[contains(text(),'" + destination + "')]"))));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
