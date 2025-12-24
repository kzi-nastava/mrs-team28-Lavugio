package com.backend.lavugio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinishRideDTO {
    private double finalPrice;
    private CoordinatesDTO finalDestination;
    private String endTime;
}
