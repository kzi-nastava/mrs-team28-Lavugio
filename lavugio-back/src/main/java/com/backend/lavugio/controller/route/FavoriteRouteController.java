package com.backend.lavugio.controller.route;

import com.backend.lavugio.dto.route.NewFavoriteRouteDTO;
import com.backend.lavugio.dto.route.UpdateFavoriteRouteDTO;
import com.backend.lavugio.security.JwtUtil;
import com.backend.lavugio.service.route.FavoriteRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorite-routes")
public class FavoriteRouteController {

    @Autowired
    private FavoriteRouteService favoriteRouteService;


    @PreAuthorize("hasRole('REGULAR_USER')")
    @PostMapping("/add")
    public ResponseEntity<?> createFavoriteRoute(@RequestBody NewFavoriteRouteDTO request) {
        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        Long accountId = JwtUtil.extractAccountId(authentication);
        try {
            NewFavoriteRouteDTO favorite = favoriteRouteService.createFavoriteRoute(accountId, request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ========== READ ==========

    @PreAuthorize("hasRole('REGULAR_USER')")
    @GetMapping("")
    public ResponseEntity<?> getFavoriteRoutes() {
        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        Long accountId = JwtUtil.extractAccountId(authentication);
        try {
            List<NewFavoriteRouteDTO> response = favoriteRouteService.getFavoriteRoutesDTOByUser(accountId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

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
    // DEPRECATED
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFavoriteRoute(
            @PathVariable Long id,
            @RequestBody UpdateFavoriteRouteDTO request) {
        try {
            //NewFavoriteRouteDTO updated = favoriteRouteService.updateFavoriteRouteDTO(id, request);
            return ResponseEntity.ok().build();
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
