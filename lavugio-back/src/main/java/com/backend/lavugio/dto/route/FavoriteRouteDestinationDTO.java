package com.backend.lavugio.dto.route;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRouteDestinationDTO {
    private Long id;
    private Long addressId;
    private String streetName;
    private String city;
    private String country;
    private int streetNumber;
    private int zipCode;
    private Double longitude;
    private Double latitude;
    private Integer destinationOrder;
}
