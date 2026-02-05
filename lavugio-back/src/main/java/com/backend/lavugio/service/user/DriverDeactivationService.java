package com.backend.lavugio.service.user;

public interface DriverDeactivationService {
    
    void checkAndDeactivateDrivers();
    
    void onStartup();
}
