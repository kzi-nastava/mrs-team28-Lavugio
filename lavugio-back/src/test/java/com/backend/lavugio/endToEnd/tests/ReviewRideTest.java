package com.backend.lavugio.endToEnd.tests;

import com.backend.lavugio.endToEnd.pages.*;
import jakarta.validation.constraints.AssertTrue;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;


public class ReviewRideTest extends TestBase{

    private static final String EMAIL = "test@gmail.com";
    private static final String PASSWORD = "perapera";

    @Test
    @Sql("reviewTestData.sql")
    public void reviewRide() throws InterruptedException {
        HomePage home = new HomePage(driver);
        home.goToLoginPage();
        LoginPage login = new LoginPage(driver);
        login.insertEmail(EMAIL);
        login.insertPassword(PASSWORD);
        login.clickSubmit();
        FindTripPage findTrip = new FindTripPage(driver);
        findTrip.clickHistoryBtn();
        PassengerHistoryPage historyPage = new PassengerHistoryPage(driver);
        historyPage.clickElement(historyPage.reviewableRideItem);
        PassengerHistoryDetailedPage detailedPage = new PassengerHistoryDetailedPage(driver);
        detailedPage.openReviewRideForm();
        detailedPage.rateVehicle(5);
        detailedPage.rateDriver(5);
        detailedPage.insertComment("great ride, recommend");
        detailedPage.clickConfirm();
        detailedPage.waitUntilFormClosed();
        Assert.assertFalse(detailedPage.isReviewButtonVisible());
    }
}
