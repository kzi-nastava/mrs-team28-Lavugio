package com.backend.lavugio.dto.route;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFavoriteRouteDTO {
    private String name;
    private List<DestinationDTO> destinations;
}
