package com.backend.lavugio.model.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverRegistrationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @Column(nullable = false)
    private Long driverId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private boolean used = false;

    @PrePersist
    public void onCreate() {
        this.expiresAt = LocalDateTime.now().plusHours(24);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}
