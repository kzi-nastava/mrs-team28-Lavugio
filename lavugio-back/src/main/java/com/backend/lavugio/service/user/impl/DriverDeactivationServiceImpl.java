package com.backend.lavugio.service.user.impl;

import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.service.user.DriverActivityService;
import com.backend.lavugio.service.user.DriverAvailabilityService;
import com.backend.lavugio.service.user.DriverDeactivationService;
import com.backend.lavugio.service.user.DriverService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.springframework.context.event.EventListener;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DriverDeactivationServiceImpl implements DriverDeactivationService {

    @Autowired
    private DriverService driverService;
    @Autowired
    private DriverAvailabilityService driverAvailabilityService;
    @Autowired
    private DriverActivityService driverActivityService;


    //@Scheduled(fixedRate = 10000) // runs every 10 seconds for testing purposes
    @Scheduled(cron = "0 */15 * * * *") //
    // runs every 15 minutes
    @Transactional
    public void checkAndDeactivateDrivers() {
        System.out.println("[" + LocalDateTime.now() + "] Checking drivers...");

        List<Driver> activeDrivers = driverService.getActiveDrivers();

        for (Driver driver : activeDrivers) {
            if (shouldDeactivateDriver(driver)) {
                driverService.deactivateDriver(driver.getId());
            }
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void onStartup() {
        System.out.println("Application started - deactivating all active drivers...");

        List<Driver> activeDrivers = driverService.getActiveDrivers();

        for (Driver driver : activeDrivers) {
            driverService.deactivateDriver(driver.getId());
        }

        System.out.println("Deactivated " + activeDrivers.size() + " drivers on startup");
    }

    private boolean shouldDeactivateDriver(Driver driver) {
        Duration activeTime = driverActivityService.getActiveTimeIn24Hours(driver.getId());
        return activeTime.compareTo(Duration.ofHours(8)) >= 0;
    }
}
