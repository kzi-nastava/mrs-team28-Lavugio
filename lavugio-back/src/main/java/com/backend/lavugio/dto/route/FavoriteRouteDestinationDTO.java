package com.backend.lavugio.dto.route;

import com.backend.lavugio.dto.CoordinatesDTO;

import jakarta.validation.Valid;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRouteDestinationDTO {
    private String street;
    private String houseNumber;
    private String city;
    private String country;
    @Valid
    private CoordinatesDTO coordinates;
}
