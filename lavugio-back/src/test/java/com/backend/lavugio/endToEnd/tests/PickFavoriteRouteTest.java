package com.backend.lavugio.endToEnd.tests;

import com.backend.lavugio.endToEnd.pages.FindTripPage;
import com.backend.lavugio.endToEnd.pages.HomePage;
import com.backend.lavugio.endToEnd.pages.LoginPage;
import org.springframework.test.context.jdbc.Sql;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertTrue;

public class PickFavoriteRouteTest extends TestBase {

    private static final String EMAIL = "marko.markovic@gmail.com";
    private static final String PASSWORD = "perapera";

    private static final String FAVORITE_ROUTE_NAME_1 = "Posao - Kuća";
    private static final String FAVORITE_ROUTE_FROM_1 = "Bulevar kralja Aleksandra 73, Beograd";
    private static final String FAVORITE_ROUTE_TO_1 = "Knez Mihailova 12, Beograd";

    private static final String FAVORITE_ROUTE_NAME_2 = "Teretana - Kuća";
    private static final String FAVORITE_ROUTE_FROM_2 = "Njegoševa 45, Beograd";
    private static final String FAVORITE_ROUTE_TO_2 = "Bulevar kralja Aleksandra 73, Beograd";

    @Test
    @Sql("favoriteRouteTestData.sql")
    public void pickFavoriteRoute() throws InterruptedException {
        HomePage home = new HomePage(driver);
        home.goToLoginPage();
        LoginPage login = new LoginPage(driver);
        login.insertEmail(EMAIL);
        login.insertPassword(PASSWORD);
        login.clickSubmit();
        FindTripPage findTrip = new FindTripPage(driver);
        //findTrip.clickSelectFavoriteRouteBtn();
        assertTrue(findTrip.isNoDestinationsAdded());
    }
}
