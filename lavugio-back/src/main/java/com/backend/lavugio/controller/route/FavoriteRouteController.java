package com.backend.lavugio.controller.route;

import com.backend.lavugio.dto.route.FavoriteRouteDTO;
import com.backend.lavugio.dto.route.UpdateFavoriteRouteDTO;
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

    @PostMapping("/")
    public ResponseEntity<?> createFavoriteRoute(@RequestBody FavoriteRouteDTO request) {
        try {
            FavoriteRouteDTO favorite = favoriteRouteService.createFavoriteRoute(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(favorite);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ========== READ ==========

    @GetMapping("/{id}")
    public ResponseEntity<?> getFavoriteRoute(@PathVariable Long id) {
        try {
            FavoriteRouteDTO favorite = favoriteRouteService.getFavoriteRouteDTOById(id);
            return ResponseEntity.ok(favorite);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getFavoriteRoutesByUser(@PathVariable Long userId) {
        try {
            List<FavoriteRouteDTO> favorites = favoriteRouteService.getFavoriteRoutesDTOByUser(userId);
            return ResponseEntity.ok(favorites);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllFavoriteRoutes() {
        try {
            List<FavoriteRouteDTO> favorites = favoriteRouteService.getAllFavoriteRoutesDTO();
            return ResponseEntity.ok(favorites);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    // ========== UPDATE ==========

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFavoriteRoute(
            @PathVariable Long id,
            @RequestBody UpdateFavoriteRouteDTO request) {
        try {
            FavoriteRouteDTO updated = favoriteRouteService.updateFavoriteRouteDTO(id, request);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ========== DELETE ==========

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFavoriteRoute(@PathVariable Long id) {
        try {
            favoriteRouteService.deleteFavoriteRoute(id);
            return ResponseEntity.ok("Favorite route deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
