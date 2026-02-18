package com.backend.lavugio.endToEnd.tests;

import com.backend.lavugio.endToEnd.pages.FindTripPage;
import com.backend.lavugio.endToEnd.pages.HomePage;
import com.backend.lavugio.endToEnd.pages.LoginPage;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.testng.annotations.Test;


import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

public class PickFavoriteRouteTest extends TestBase {

    private static final String EMAIL = "marko.markovic@gmail.com";
    private static final String EMAIL_NO_FAVORITE_ROUTES = "no.favorite.routes@gmail.com";
    private static final String PASSWORD = "perapera";

    private static final String FAVORITE_ROUTE_NAME_1 = "Route 1";
    private static final String FAVORITE_ROUTE_1_DESTINATION_1 = "Bulevar kralja Aleksandra 73, Beograd";
    private static final String FAVORITE_ROUTE_1_DESTINATION_2 = "Knez Mihailova 12, Beograd";

    private static final String FAVORITE_ROUTE_NAME_2 = "Route 2";
    private static final String FAVORITE_ROUTE_2_DESTINATION_1 = "Njegoševa 45, Beograd";
    private static final String FAVORITE_ROUTE_2_DESTINATION_2 = "Terazije 5, Beograd";
    private static final String FAVORITE_ROUTE_2_DESTINATION_3 = "Skadarska 22, Beograd";

    @Test
    @Sql(scripts = "favoriteRouteTestData.sql",
         config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
    public void pickFavoriteRoute() throws InterruptedException {
        HomePage home = new HomePage(driver);
        home.goToLoginPage();
        LoginPage login = new LoginPage(driver);
        login.insertEmail(EMAIL_NO_FAVORITE_ROUTES);
        login.insertPassword(PASSWORD);
        login.clickSubmit();


        FindTripPage findTrip = new FindTripPage(driver);

        // CHECKING IF NO DESTINATIONS ARE ADDED BEFORE PICKING FAVORITE ROUTE
        assertTrue(findTrip.isNoDestinationsAdded());
        findTrip.clickOpenFavoriteRoutesBtn();
        assertTrue(findTrip.noFavoriteRoutes());
        findTrip.closeFavoriteRouteDialog();

        findTrip.logout();
        // LOGGING IN WITH USER THAT HAS FAVORITE ROUTES
        home.goToLoginPage();
        login.insertEmail(EMAIL);
        login.insertPassword(PASSWORD);
        login.clickSubmit();

        // PICKING FAVORITE ROUTE FOR THE FIRST TIME
        findTrip.clickOpenFavoriteRoutesBtn();
        findTrip.selectFavoriteRoute(FAVORITE_ROUTE_NAME_1);
        findTrip.isSelectFavoriteRouteBtnEnabled();
        findTrip.clickSelectFavoriteRouteBtn();
        assertTrue(findTrip.isDestinationAdded(FAVORITE_ROUTE_1_DESTINATION_1));
        assertTrue(findTrip.isDestinationAdded(FAVORITE_ROUTE_1_DESTINATION_2));

        assertFalse(findTrip.isDestinationAdded(FAVORITE_ROUTE_2_DESTINATION_1));
        assertFalse(findTrip.isDestinationAdded(FAVORITE_ROUTE_2_DESTINATION_2));
        assertFalse(findTrip.isDestinationAdded(FAVORITE_ROUTE_2_DESTINATION_3));

        // PICKING FAVORITE ROUTE FOR THE SECOND TIME
        findTrip.clickOpenFavoriteRoutesBtn();
        findTrip.selectFavoriteRoute(FAVORITE_ROUTE_NAME_2);
        findTrip.isSelectFavoriteRouteBtnEnabled();
        findTrip.clickSelectFavoriteRouteBtn();
        assertTrue(findTrip.isDestinationAdded(FAVORITE_ROUTE_2_DESTINATION_1));
        assertTrue(findTrip.isDestinationAdded(FAVORITE_ROUTE_2_DESTINATION_2));
        assertTrue(findTrip.isDestinationAdded(FAVORITE_ROUTE_2_DESTINATION_3));

        assertFalse(findTrip.isDestinationAdded(FAVORITE_ROUTE_1_DESTINATION_1));
        assertFalse(findTrip.isDestinationAdded(FAVORITE_ROUTE_1_DESTINATION_2));
    }
}
