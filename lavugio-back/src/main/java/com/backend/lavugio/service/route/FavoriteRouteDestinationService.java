package com.backend.lavugio.service.route;

import com.backend.lavugio.model.route.FavoriteRouteDestination;
import com.backend.lavugio.model.route.FavoriteRoute;

import java.util.List;

public interface FavoriteRouteDestinationService {
    FavoriteRouteDestination addDestinationToRoute(FavoriteRouteDestination destination);
    void removeDestination(Long destinationId);
    List<FavoriteRouteDestination> getDestinationsByRoute(FavoriteRoute route);
    List<FavoriteRouteDestination> getDestinationsByRouteId(Long routeId);
    void reorderDestinations(Long routeId, List<Long> destinationIdsInOrder);
    void clearRouteDestinations(Long routeId);
    int getNextDestinationOrder(Long routeId);
}