package com.backend.lavugio.service.route.impl;

import com.backend.lavugio.model.route.FavoriteRoute;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.repository.route.FavoriteRouteRepository;
import com.backend.lavugio.repository.route.FavoriteRouteDestinationRepository;
import com.backend.lavugio.repository.user.RegularUserRepository;
import com.backend.lavugio.service.route.FavoriteRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteRouteServiceImpl implements FavoriteRouteService {

    @Autowired
    private FavoriteRouteRepository favoriteRouteRepository;

    @Autowired
    private FavoriteRouteDestinationRepository destinationRepository;

    @Autowired
    private RegularUserRepository regularUserRepository;

    @Override
    @Transactional
    public FavoriteRoute createFavoriteRoute(FavoriteRoute favoriteRoute) {
        // Provera da li korisnik postoji
        if (favoriteRoute.getUser() == null || favoriteRoute.getUser().getId() == 0) {
            throw new RuntimeException("User is required for creating a favorite route");
        }

        // Provera da li ime rute već postoji za ovog korisnika
        if (isRouteNameTakenForUser(favoriteRoute.getName(), favoriteRoute.getUser().getId())) {
            throw new RuntimeException("Route name already exists for this user");
        }

        return favoriteRouteRepository.save(favoriteRoute);
    }

    @Override
    @Transactional
    public FavoriteRoute updateFavoriteRoute(Long routeId, FavoriteRoute favoriteRoute) {
        FavoriteRoute existing = favoriteRouteRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Favorite route not found with id: " + routeId));

        // Provera da li novo ime već postoji (ako se ime menja)
        if (!existing.getName().equals(favoriteRoute.getName()) &&
                isRouteNameTakenForUser(favoriteRoute.getName(), existing.getUser().getId())) {
            throw new RuntimeException("Route name already exists for this user");
        }

        existing.setName(favoriteRoute.getName());
        return favoriteRouteRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteFavoriteRoute(Long routeId, Long userId) {
        // Provera da li ruta pripada korisniku
        FavoriteRoute route = favoriteRouteRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Favorite route not found"));

        if (!route.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only delete your own routes");
        }

        // Prvo obrišite sve destinacije
        destinationRepository.deleteByFavoriteRouteId(routeId);

        // Onda obrišite rutu
        favoriteRouteRepository.delete(route);
    }

    @Override
    public FavoriteRoute getFavoriteRouteById(Long id) {
        return favoriteRouteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Favorite route not found with id: " + id));
    }

    @Override
    public List<FavoriteRoute> getFavoriteRoutesByUser(RegularUser user) {
        return favoriteRouteRepository.findByUser(user);
    }

    @Override
    public List<FavoriteRoute> getFavoriteRoutesByUserId(Long userId) {
        return favoriteRouteRepository.findByUserId(userId);
    }

    @Override
    public boolean isRouteNameTakenForUser(String name, Long userId) {
        return favoriteRouteRepository.existsByNameAndUserId(name, userId);
    }

    @Override
    @Transactional
    public void addDestinationToRoute(Long routeId, Long addressId, Integer order) {
        // Ova metoda bi zahtevala dodatnu logiku i DTO modele
        // Za sada je samo placeholder
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    @Override
    @Transactional
    public void removeDestinationFromRoute(Long routeId, Long destinationId) {
        // Provera da li destinacija pripada ruti
        // Ova metoda bi zahtevala dodatnu logiku
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    @Override
    public List<Long> getRouteDestinations(Long routeId) {
        return destinationRepository.findAddressIdsByFavoriteRouteId(routeId);
    }

    @Override
    public int getRouteDestinationCount(Long routeId) {
        return destinationRepository.countByFavoriteRouteId(routeId);
    }
}