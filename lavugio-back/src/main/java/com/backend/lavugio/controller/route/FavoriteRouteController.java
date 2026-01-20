package com.backend.lavugio.controller.route;

import com.backend.lavugio.dto.route.NewFavoriteRouteDTO;
import com.backend.lavugio.dto.route.UpdateFavoriteRouteDTO;
import com.backend.lavugio.service.route.FavoriteRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorite-routes")
public class FavoriteRouteController {

    @Autowired
    private FavoriteRouteService favoriteRouteService;

    // FOR TESTING
    final private Long accountID = 1L;

    @PostMapping("/add")
    public ResponseEntity<?> createFavoriteRoute(@RequestBody NewFavoriteRouteDTO request) {
        // Authentication auth
        try {
            NewFavoriteRouteDTO favorite = favoriteRouteService.createFavoriteRoute(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ========== READ ==========

    @GetMapping("/{id}")
    public ResponseEntity<?> getFavoriteRoute(@PathVariable Long id) {
        try {
            NewFavoriteRouteDTO favorite = favoriteRouteService.getFavoriteRouteDTOById(id);
            return ResponseEntity.ok(favorite);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getFavoriteRoutesByUser(@PathVariable Long userId) {
        try {
            List<NewFavoriteRouteDTO> favorites = favoriteRouteService.getFavoriteRoutesDTOByUser(userId);
            return ResponseEntity.ok(favorites);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllFavoriteRoutes() {
        try {
            List<NewFavoriteRouteDTO> favorites = favoriteRouteService.getAllFavoriteRoutesDTO();
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
            NewFavoriteRouteDTO updated = favoriteRouteService.updateFavoriteRouteDTO(id, request);
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
