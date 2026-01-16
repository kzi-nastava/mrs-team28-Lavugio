package com.backend.lavugio.dto.route;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DestinationDTO {
    private String streetName;
    private String city;
    private String country;
    private int streetNumber;
    private int zipCode;
    private Double longitude;
    private Double latitude;
    private Integer order;
}
