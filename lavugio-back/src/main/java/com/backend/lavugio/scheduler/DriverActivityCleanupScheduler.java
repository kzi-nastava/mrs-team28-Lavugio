package com.backend.lavugio.scheduler;

import com.backend.lavugio.repository.user.DriverActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@EnableScheduling
public class DriverActivityCleanupScheduler {

    @Autowired
    private DriverActivityRepository activityRepository;

    @Scheduled(cron = "0 0 2 * * *") // Every day at 02:00
    public void cleanupOldActivities() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        activityRepository.deleteByStartTimeBefore(cutoff);
        System.out.println("Driver Activities older than 24h deleted.");
    }
}