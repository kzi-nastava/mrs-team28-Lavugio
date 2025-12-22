package com.backend.lavugio.controller.route;

import com.backend.lavugio.service.route.FavoriteRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/favorite-routes")
public class FavoriteRouteController {

    @Autowired
    private FavoriteRouteService favoriteRouteService;

    @GetMapping("/{id}")
    public FavoriteRouteDTO getFavoriteRoute(@PathVariable Long id) {
        List<Long> favoriteRouteDestinationsIds = favoriteRouteService.getRouteDestinations(id);
        // DOBAVI SVE DESTINACIJE RUTE SA PROSLEDJENIM ID
        return new FavoriteRouteDTO(id, favoriteRouteDestinationsIds);
    }
}
