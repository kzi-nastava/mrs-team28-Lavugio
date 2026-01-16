package com.backend.lavugio.dto.ride;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class RideCalculatedInfoDTO {
    private float price;
    private float distance;
    private int estimatedTimeMinutes;
}
