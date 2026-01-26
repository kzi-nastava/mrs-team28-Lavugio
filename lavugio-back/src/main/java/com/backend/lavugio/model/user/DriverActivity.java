package com.backend.lavugio.model.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "driver_activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private boolean endedActivity;

    public DriverActivity(Driver driver) {
        this.driver = driver;
        this.startTime = LocalDateTime.now();
        this.endedActivity = false;
    }
}