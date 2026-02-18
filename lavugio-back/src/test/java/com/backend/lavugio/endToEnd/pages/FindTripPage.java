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

    @FindBy(xpath = "//button[text()=' Cancel ']")
    WebElement cancelSelectFavoriteRouteBtn;

    @FindBy(xpath = "//button[text()=' Delete ']")
    WebElement deleteFavoriteRouteBtn;

    @FindBy(tagName = "app-destinations-display")
    WebElement destinationsDisplay;

    @FindBy(id = "map")
    WebElement map;
    // Navbar element to verify page loaded
    @FindBy(xpath = "//app-navbar")
    WebElement navbar;

    public FindTripPage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(this.driver, this);
        waitForPageToLoad();
    }

    public void logout() {
        By logoutBtnLocator = By.xpath("//button[contains(text(),'Logout')]");

        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
        WebElement logoutBtn = wait.until(ExpectedConditions.elementToBeClickable(logoutBtnLocator));
        logoutBtn.click();

        // Optional: wait until we're actually logged out (login button or route changes)
        wait.until(ExpectedConditions.invisibilityOfElementLocated(logoutBtnLocator));
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

    public void clickOrderRideBtn() {
        new WebDriverWait(this.driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(orderRideBtn)).click();
    }

    public void clickOpenFavoriteRoutesBtn() {
        new WebDriverWait(this.driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(openFavoriteRoutesBtn)).click();
    }

    public boolean isNoDestinationsAdded() {
        try {
            new WebDriverWait(this.driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//app-destinations-display//p[contains(text(),'No destinations added yet')]")
                    ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void closeFavoriteRouteDialog() {
        new WebDriverWait(this.driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(cancelSelectFavoriteRouteBtn)).click();
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

    public boolean noFavoriteRoutes() {
        try {
            new WebDriverWait(this.driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//div[contains(text(),'No favorite routes available')]")
                    ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
