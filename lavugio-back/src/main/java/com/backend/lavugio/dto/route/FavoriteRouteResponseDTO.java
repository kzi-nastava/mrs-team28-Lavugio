package com.backend.lavugio.dto.route;

import com.backend.lavugio.dto.ride.RideDestinationDTO;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteRouteResponseDTO {
    private Long id;
    private String name;

    private List<RideDestinationDTO> favoriteRouteStops;
}
