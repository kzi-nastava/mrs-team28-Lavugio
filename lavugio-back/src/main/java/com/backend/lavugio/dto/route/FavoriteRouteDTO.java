package com.backend.lavugio.dto.route;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRouteDTO {
    private Long id;
    private String name;
    private Long userId;
    private String userName;
    private List<FavoriteRouteDestinationDTO> destinations;
}
