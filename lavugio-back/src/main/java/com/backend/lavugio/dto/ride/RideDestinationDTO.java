package com.backend.lavugio.dto.ride;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RideDestinationDTO {
    private StopBaseDTO location;
    private String address;

    private String streetName;
    private String city;
    private String country;
    private int streetNumber;
    private int zipCode;
}
