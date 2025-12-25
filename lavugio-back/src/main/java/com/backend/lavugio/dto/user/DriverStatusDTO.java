package com.backend.lavugio.dto.user;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DriverStatusDTO {
    private Long driverId;
    private boolean isActive;
    private Long activeMinutesLast24h;
    private Long remainingMinutesToday;
    private LocalDateTime lastStatusChange;
    private boolean canActivate;
}