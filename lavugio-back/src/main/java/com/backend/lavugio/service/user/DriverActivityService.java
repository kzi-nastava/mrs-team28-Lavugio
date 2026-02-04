package com.backend.lavugio.service.user;

import com.backend.lavugio.model.user.DriverActivity;

import java.time.Duration;

public interface DriverActivityService {
    void startActivity(Long driverId);
    void endActivity(Long driverId);
    void endActivityForAllDrivers();
    Duration getActiveTimeIn24Hours(Long driverId);
    Duration calculateDuration(DriverActivity activity);
    String getFormattedActiveTime(Long driverId);
}
