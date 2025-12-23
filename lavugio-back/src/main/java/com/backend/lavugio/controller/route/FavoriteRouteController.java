package com.backend.lavugio.controller.route;

import com.backend.lavugio.dto.route.FavoriteRouteRequestDTO;
import com.backend.lavugio.dto.route.FavoriteRouteResponseDTO;
import com.backend.lavugio.model.route.FavoriteRoute;
import com.backend.lavugio.service.route.FavoriteRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite-routes")
public class FavoriteRouteController {

    @Autowired
    private FavoriteRouteService favoriteRouteService;

    @GetMapping("/{id}")
    public FavoriteRouteResponseDTO getFavoriteRoute(@PathVariable Long id) {
        List<Long> favoriteRouteDestinationsIds = favoriteRouteService.getRouteDestinations(id);

        FavoriteRouteResponseDTO favoriteRouteDto = new FavoriteRouteResponseDTO();
        return favoriteRouteDto;
    }

    @GetMapping("/{userId}")
    public FavoriteRouteResponseDTO getFavoriteRouteByUser() {
        // @AuthenticationPrincipal UserDetails userDetails
        Long id = 1L; // Placeholder vrednost
        List<FavoriteRoute> userFavoriteRoutes = favoriteRouteService.getFavoriteRoutesByUserId(id);
        // ODRADI KONVERZIJU U DTO
        List<FavoriteRouteResponseDTO> favoriteRoutesDto;
        return favoriteRoutesDto;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addFavoriteRoute(
            @RequestBody FavoriteRouteRequestDTO request) {
        // @AuthenticationPrincipal UserDetails userDetails,
        try {
            // String userEmail = userDetails.getUsername();
            // FavoriteRouteDTO favorite = favoriteRouteService.addFavoriteRoute(userEmail, request);
            FavoriteRoute favorite = new FavoriteRoute(); // Placeholder vrednost
            return ResponseEntity.status(HttpStatus.CREATED).body(favorite);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
