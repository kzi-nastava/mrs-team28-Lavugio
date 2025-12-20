package com.backend.lavugio.model.ride;

import jakarta.persistence.*;

@Entity
@Table(name = "ride_reports")
public class RideReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long reportId;

    @ManyToOne()
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @Column(nullable = false)
    private String reportMessage;
}
