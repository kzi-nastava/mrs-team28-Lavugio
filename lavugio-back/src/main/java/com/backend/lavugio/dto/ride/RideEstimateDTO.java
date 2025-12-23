package com.backend.lavugio.dto.ride;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RideEstimateDTO {
    private float price;
    private float distance;
    private int estimatedTimeMinutes;
}
