package com.backend.lavugio.service.user.impl;

import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.net.SyslogOutputStream;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.DriverActivity;
import com.backend.lavugio.repository.user.DriverActivityRepository;
import com.backend.lavugio.repository.user.DriverRepository;
import com.backend.lavugio.service.user.DriverActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DriverActivityServiceImpl implements DriverActivityService {
    @Autowired
    private DriverActivityRepository activityRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Override
    public void startActivity(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        if (driver.isBlocked()) {
            throw new RuntimeException("Blocked driver cannot be active");
        }

        // Check if driver is already active
        Optional<DriverActivity> existing =
                activityRepository.findByDriverAndEndedActivity(driver, false);

        if (existing.isEmpty()) {
            DriverActivity activity = new DriverActivity(driver);
            activityRepository.save(activity);

            driver.setActive(true);
            driverRepository.save(driver);
        }
    }

    @Override
    public void endActivity(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        activityRepository.findByDriverAndEndedActivity(driver, false)
                .ifPresent(activity -> {
                    activity.setEndTime(LocalDateTime.now());
                    activity.setEndedActivity(true);
                    activityRepository.save(activity);
                });

        driver.setActive(false);
        driverRepository.save(driver);
        System.out.printf("Ended activity for driver: %s%n", driver.getName() + driver.getLastName());
    }

    @Transactional
    public void endActivityForAllDrivers() {
        List<Driver> activeDrivers = driverRepository.findAll();

        for (Driver driver : activeDrivers) {
            activityRepository.findByDriverAndEndedActivity(driver, false)
                    .ifPresent(activity -> {
                        activity.setEndTime(LocalDateTime.now());
                        activity.setEndedActivity(true);
                        activityRepository.save(activity);
                    });

            if (driver.isActive()) {
                driver.setActive(false);
                driverRepository.save(driver);
            }
        }
    }

    @Override
    public Duration getActiveTimeIn24Hours(Long driverId) {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        List<DriverActivity> activities =
                activityRepository.findByDriverIdAndStartTimeAfter(driverId, cutoff);

        return activities.stream()
                .map(this::calculateDuration)
                .reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public Duration calculateDuration(DriverActivity activity) {
        LocalDateTime end = activity.getEndTime() != null
                ? activity.getEndTime()
                : LocalDateTime.now();
        return Duration.between(activity.getStartTime(), end);
    }

    @Override
    public String getFormattedActiveTime(Long driverId) {
        Duration duration = getActiveTimeIn24Hours(driverId);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return String.format("%d hours and %d minutes", hours, minutes);
    }
}
