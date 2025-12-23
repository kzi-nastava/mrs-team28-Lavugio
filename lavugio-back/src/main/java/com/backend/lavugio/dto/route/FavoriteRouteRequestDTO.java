package com.backend.lavugio.dto.route;

import com.backend.lavugio.dto.ride.StopBaseDTO;

import java.util.List;

public class FavoriteRouteRequestDTO {
    private String name;
    List<StopBaseDTO> stops;
}
