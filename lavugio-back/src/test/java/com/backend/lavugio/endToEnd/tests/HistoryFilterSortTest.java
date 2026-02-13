package com.backend.lavugio.endToEnd.tests;

import com.backend.lavugio.endToEnd.pages.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testng.Assert;
import org.testng.annotations.Test;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class HistoryFilterSortTest extends TestBase {

    private static final String TEST_USER_EMAIL = "filtertest@gmail.com";
    private static final String TEST_USER_PASSWORD = "perapera";
    private static final String EMPTY_HISTORY_USER_EMAIL = "emptyhistory@gmail.com";
    private static final String EMPTY_HISTORY_USER_PASSWORD = "perapera";

    // Helper: login and go to history
    private HistoryFilterSortPage loginAndNavigateToHistory(String email, String password) {
        HomePage home = new HomePage(driver);
        home.goToLoginPage();
        LoginPage login = new LoginPage(driver);
        login.insertEmail(email);
        login.insertPassword(password);
        login.clickSubmit();
        FindTripPage findTrip = new FindTripPage(driver);
        findTrip.clickHistoryBtn();
        HistoryFilterSortPage historyPage = new HistoryFilterSortPage(driver);
        historyPage.waitForPageToLoad();
        return historyPage;
    }

    // 1. Happy path: page loads, controls visible
    @Test(priority = 1)
    @Sql("historyFilterSortTestData.sql")
    public void testHistoryPageDisplaysRidesAndControls() {
        HistoryFilterSortPage historyPage = loginAndNavigateToHistory(TEST_USER_EMAIL, TEST_USER_PASSWORD);
        Assert.assertTrue(historyPage.isDatePickerDisplayed(), "Date picker should be visible");
        Assert.assertTrue(historyPage.areSortingHeadersDisplayed(), "Sorting headers should be visible");
        Assert.assertTrue(historyPage.getRideCount() > 0, "User should have rides displayed");
    }

    // 2. Happy path: filter by January 2026
    @Test(priority = 2)
    @Sql("historyFilterSortTestData.sql")
    public void testFilterByDateRangeJanuary() {
        HistoryFilterSortPage historyPage = loginAndNavigateToHistory(TEST_USER_EMAIL, TEST_USER_PASSWORD);
        int initialCount = historyPage.getRideCount();
        historyPage.selectDateRange("01/01/2026", "31/01/2026");
        int filteredCount = historyPage.getRideCount();
        Assert.assertTrue(filteredCount <= initialCount, "Filtered count should be less or equal");
        Assert.assertTrue(filteredCount > 0, "Should have rides in January 2026");
    }

    // 3. Happy path: sort by Start date descending
    @Test(priority = 3)
    @Sql("historyFilterSortTestData.sql")
    public void testSortByStartDateDescending() {
        HistoryFilterSortPage historyPage = loginAndNavigateToHistory(TEST_USER_EMAIL, TEST_USER_PASSWORD);
        Assert.assertTrue(historyPage.isSortIndicatorVisibleForStart(), "Sort indicator should be visible for Start");
        Assert.assertTrue(historyPage.areDatesSortedDescending(), "Dates should be sorted descending");
    }

    // 4. Happy path: sort by Pickup
    @Test(priority = 4)
    @Sql("historyFilterSortTestData.sql")
    public void testSortByPickup() {
        HistoryFilterSortPage historyPage = loginAndNavigateToHistory(TEST_USER_EMAIL, TEST_USER_PASSWORD);
        historyPage.clickSortByPickup();
        Assert.assertTrue(historyPage.isSortIndicatorVisibleForPickup(), "Sort indicator should be visible for Pickup");
    }

    // 5. Exception: user with no ride history
    @Test(priority = 5)
    @Sql("historyFilterSortTestData.sql")
    public void testEmptyHistoryUser() {
        HistoryFilterSortPage historyPage = loginAndNavigateToHistory(EMPTY_HISTORY_USER_EMAIL, EMPTY_HISTORY_USER_PASSWORD);
        int rideCount = historyPage.getRideCount();
        boolean noRidesShown = historyPage.isNoRidesMessageDisplayed();
        Assert.assertTrue(rideCount == 0 || noRidesShown, "User with no history should see empty state");
    }

    // 6. Exception: filter with no results (March 2026)
    @Test(priority = 6)
    @Sql("historyFilterSortTestData.sql")
    public void testFilterNoResults() {
        HistoryFilterSortPage historyPage = loginAndNavigateToHistory(TEST_USER_EMAIL, TEST_USER_PASSWORD);
        historyPage.selectDateRange("01/03/2026", "31/03/2026");
        int rideCount = historyPage.getRideCount();
        boolean noRidesShown = historyPage.isNoRidesMessageDisplayed();
        Assert.assertTrue(rideCount == 0 || noRidesShown, "Should show no rides or display 'no rides' message");
    }
}
