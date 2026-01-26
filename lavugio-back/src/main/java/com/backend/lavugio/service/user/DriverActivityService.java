package com.backend.lavugio.service.user;

import com.backend.lavugio.model.user.DriverActivity;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public interface DriverActivityService {
    void startActivity(Long driverId);
    void endActivity(Long driverId);
    Duration getActiveTimeIn24Hours(Long driverId);
    Duration calculateDuration(DriverActivity activity);
    String getFormattedActiveTime(Long driverId);
}
