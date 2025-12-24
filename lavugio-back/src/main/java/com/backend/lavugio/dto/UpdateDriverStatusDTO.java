package com.backend.lavugio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDriverStatusDTO {
    private double longitude;
    private double latitude;
    private boolean available;
}
