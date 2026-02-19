package com.example.lavugio_mobile;

import android.app.Application;

import com.example.lavugio_mobile.api.ApiClient;
import com.example.lavugio_mobile.services.DriverService;
import com.example.lavugio_mobile.services.LocationService;
import com.example.lavugio_mobile.services.NotificationService;
import com.example.lavugio_mobile.services.PriceService;
import com.example.lavugio_mobile.services.RideService;
import com.example.lavugio_mobile.services.UserService;
import com.example.lavugio_mobile.services.WebSocketService;
import com.example.lavugio_mobile.services.auth.AuthService;
import com.example.lavugio_mobile.services.auth.SessionManager;

public class LavugioApp extends Application {

    private static WebSocketService webSocketService;
    private static LocationService locationService;
    private static RideService rideService;
    private static DriverService driverService;
    private static UserService userService;
    private static PriceService priceService;
    private static NotificationService notificationService;

    @Override
    public void onCreate() {
        super.onCreate();

        SessionManager sessionManager = new SessionManager(this);

        ApiClient.init(sessionManager::getToken);

        webSocketService = new WebSocketService();
        webSocketService.setSessionManager(sessionManager);

        locationService = new LocationService(this);

        rideService = new RideService(webSocketService);
        driverService = new DriverService(locationService);
        userService = new UserService();
        priceService = new PriceService();
        notificationService = new NotificationService();

        AuthService.init(this, webSocketService);
    }

    // ── Global accessors ─────────────────────────────────

    public static WebSocketService getWebSocketService() {
        return webSocketService;
    }

    public static LocationService getLocationService() {
        return locationService;
    }

    public static RideService getRideService() {
        return rideService;
    }

    public static DriverService getDriverService() {
        return driverService;
    }

    public static UserService getUserService(){return userService;}

    public static PriceService getPriceService(){return priceService;}

    public static NotificationService getNotificationService() { return notificationService; }
}