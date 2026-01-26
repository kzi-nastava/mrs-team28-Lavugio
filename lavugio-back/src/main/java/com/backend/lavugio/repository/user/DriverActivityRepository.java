package com.backend.lavugio.repository.user;

import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.DriverActivity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverActivityRepository extends JpaRepository<DriverActivity, Long> {

    Optional<DriverActivity> findByDriverAndEndedActivity(Driver driver, boolean endedActivity);

    List<DriverActivity> findByDriverIdAndStartTimeAfter(Long driverId, LocalDateTime startTime);

    @Modifying
    @Transactional
    void deleteByStartTimeBefore(LocalDateTime cutoffTime);
}