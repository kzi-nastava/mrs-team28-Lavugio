package com.backend.lavugio.service.route;

import com.backend.lavugio.model.route.FavoriteRoute;
import com.backend.lavugio.model.user.RegularUser;

import java.util.List;

public interface FavoriteRouteService {
    FavoriteRoute createFavoriteRoute(FavoriteRoute favoriteRoute);
    FavoriteRoute updateFavoriteRoute(Long routeId, FavoriteRoute favoriteRoute);
    void deleteFavoriteRoute(Long routeId, Long userId);
    FavoriteRoute getFavoriteRouteById(Long id);
    List<FavoriteRoute> getFavoriteRoutesByUser(RegularUser user);
    List<FavoriteRoute> getFavoriteRoutesByUserId(Long userId);
    boolean isRouteNameTakenForUser(String name, Long userId);
    void addDestinationToRoute(Long routeId, Long addressId, Integer order);
    void removeDestinationFromRoute(Long routeId, Long destinationId);
    List<Long> getRouteDestinations(Long routeId);
    int getRouteDestinationCount(Long routeId);
}