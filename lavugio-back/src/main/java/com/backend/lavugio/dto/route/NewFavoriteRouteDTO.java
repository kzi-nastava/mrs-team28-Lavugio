package com.backend.lavugio.dto.route;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewFavoriteRouteDTO {
    private String name;;
    private List<FavoriteRouteDestinationDTO> destinations;
}
