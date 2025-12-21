package com.backend.lavugio.model.notification;

import com.backend.lavugio.model.user.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String text;

    @Column(name = "link_to_ride")
    private String linkToRide;

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

    @Column(name = "is_read", nullable = false)
    private boolean read = false;
}