package com.backend.lavugio.endToEnd.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class PassengerHistoryDetailedPage {

    private WebDriver driver;

    private WebDriverWait wait;

    @FindBy(xpath = "//button[contains(text(),'Review Ride')]")
    WebElement reviewRideBtn;

    @FindBy(xpath = "//label[text()='Driver rating']/following-sibling::div")
    WebElement driverRatingContainer;

    @FindBy(xpath = "//label[text()='Vehicle rating']/following-sibling::div")
    WebElement vehicleRatingContainer;

    @FindBy(xpath = "//textarea[@maxlength='256']")
    WebElement commentArea;

    @FindBy(xpath = "//button[contains(text(),'Confirm')]")
    WebElement confirmBtn;

    public PassengerHistoryDetailedPage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(this.driver, this);
        this.wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
    }

    public void openReviewRideForm(){
        wait.until(ExpectedConditions.elementToBeClickable(reviewRideBtn)).click();
    }

    public void rateDriver(int rating){
        WebElement starBtn = driverRatingContainer.findElement(By.xpath(String.format("(.//button)[%d]",rating)));
        wait.until(ExpectedConditions.elementToBeClickable(starBtn)).click();
    }

    public void rateVehicle(int rating){
        WebElement starBtn = vehicleRatingContainer.findElement(By.xpath(String.format("(.//button)[%d]",rating)));
        wait.until(ExpectedConditions.elementToBeClickable(starBtn)).click();
    }

    public void insertComment(String comment){
        commentArea.clear();
        commentArea.sendKeys(comment);
    }

    public void clickConfirm(){
        wait.until(ExpectedConditions.elementToBeClickable(confirmBtn)).click();
    }

    public void waitUntilFormClosed(){
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//app-review-form")
        ));
    }

    public boolean isReviewButtonVisible(){
        try {
            return reviewRideBtn.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
