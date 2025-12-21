package com.backend.lavugio.model.user;

import com.backend.lavugio.model.route.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverStatus {
    private Long driverId;
    private double longitude;
    private double latitude;
    private boolean isAvailable;
}
