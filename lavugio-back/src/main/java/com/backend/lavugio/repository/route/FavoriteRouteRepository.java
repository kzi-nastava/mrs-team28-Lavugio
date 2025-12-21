package com.backend.lavugio.repository.route;

import com.backend.lavugio.model.route.FavoriteRoute;
import com.backend.lavugio.model.user.RegularUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRouteRepository extends JpaRepository<FavoriteRoute, Long> {
    List<FavoriteRoute> findByUser(RegularUser user);

    List<FavoriteRoute> findByUserId(Long userId);

    Optional<FavoriteRoute> findByNameAndUser(String name, RegularUser user);

    @Query("SELECT COUNT(fr) > 0 FROM FavoriteRoute fr WHERE fr.name = :name AND fr.user.id = :userId")
    boolean existsByNameAndUserId(String name, Long userId);

    @Query("SELECT fr FROM FavoriteRoute fr WHERE fr.user.id = :userId ORDER BY fr.name ASC")
    List<FavoriteRoute> findByUserIdOrderByName(Long userId);

    @Query("SELECT COUNT(fr) FROM FavoriteRoute fr WHERE fr.user.id = :userId")
    long countByUserId(Long userId);

    void deleteByUserIdAndId(Long userId, Long routeId);
}