package com.backend.lavugio.model.notification;

import com.backend.lavugio.model.user.Account;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String text;

    @Column(nullable = true)
    private String lingToRide;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Account sentTo;

    @Column
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Column(nullable = false)
    private LocalDate sentDate;

    @Column(nullable = false)
    private LocalTime sentTime;
}
