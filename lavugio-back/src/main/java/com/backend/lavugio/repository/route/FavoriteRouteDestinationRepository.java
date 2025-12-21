package com.backend.lavugio.repository.route;

import com.backend.lavugio.model.route.FavoriteRoute;
import com.backend.lavugio.model.route.FavoriteRouteDestination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRouteDestinationRepository extends JpaRepository<FavoriteRouteDestination, Long> {
    List<FavoriteRouteDestination> findByFavoriteRoute(FavoriteRoute favoriteRoute);

    List<FavoriteRouteDestination> findByFavoriteRouteId(Long favoriteRouteId);

    @Query("SELECT frd FROM FavoriteRouteDestination frd " +
            "WHERE frd.favoriteRoute.id = :routeId " +
            "ORDER BY frd.destinationOrder ASC")
    List<FavoriteRouteDestination> findByFavoriteRouteIdOrderByDestinationOrder(Long routeId);

    @Query("SELECT frd.address.id FROM FavoriteRouteDestination frd " +
            "WHERE frd.favoriteRoute.id = :routeId " +
            "ORDER BY frd.destinationOrder ASC")
    List<Long> findAddressIdsByFavoriteRouteId(Long routeId);

    void deleteByFavoriteRouteId(Long favoriteRouteId);

    @Query("SELECT COUNT(frd) FROM FavoriteRouteDestination frd WHERE frd.favoriteRoute.id = :routeId")
    int countByFavoriteRouteId(Long routeId);

    @Query("SELECT MAX(frd.destinationOrder) FROM FavoriteRouteDestination frd WHERE frd.favoriteRoute.id = :routeId")
    Integer findMaxDestinationOrderByFavoriteRouteId(Long routeId);
}