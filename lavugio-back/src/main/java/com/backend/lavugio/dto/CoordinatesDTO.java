package com.backend.lavugio.dto;

import com.backend.lavugio.model.route.Address;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoordinatesDTO {
    private double latitude;
    private double longitude;
    public CoordinatesDTO(Address address){
        this.latitude = address.getLatitude();
        this.longitude = address.getLongitude();
    }
}
