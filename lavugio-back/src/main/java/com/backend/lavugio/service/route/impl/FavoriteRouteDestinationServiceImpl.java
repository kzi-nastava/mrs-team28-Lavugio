package com.backend.lavugio.service.route.impl;

import com.backend.lavugio.model.route.FavoriteRouteDestination;
import com.backend.lavugio.model.route.FavoriteRoute;
import com.backend.lavugio.repository.route.FavoriteRouteDestinationRepository;
import com.backend.lavugio.repository.route.FavoriteRouteRepository;
import com.backend.lavugio.service.route.FavoriteRouteDestinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteRouteDestinationServiceImpl implements FavoriteRouteDestinationService {

    @Autowired
    private FavoriteRouteDestinationRepository destinationRepository;

    @Autowired
    private FavoriteRouteRepository favoriteRouteRepository;

    @Override
    @Transactional
    public FavoriteRouteDestination addDestinationToRoute(FavoriteRouteDestination destination) {
        // Provera da li ruta postoji
        FavoriteRoute route = favoriteRouteRepository.findById(destination.getFavoriteRoute().getId())
                .orElseThrow(() -> new RuntimeException("Favorite route not found"));

        // Ako nije naveden redosled, postavi na sledeći
        if (destination.getDestinationOrder() == null) {
            Integer maxOrder = destinationRepository.findMaxDestinationOrderByFavoriteRouteId(route.getId());
            destination.setDestinationOrder(maxOrder != null ? maxOrder + 1 : 1);
        }

        return destinationRepository.save(destination);
    }

    @Override
    @Transactional
    public void removeDestination(Long destinationId) {
        FavoriteRouteDestination destination = destinationRepository.findById(destinationId)
                .orElseThrow(() -> new RuntimeException("Destination not found"));

        destinationRepository.delete(destination);

        // TODO: Ponovo poredaj preostale destinacije
        reorderRemainingDestinations(destination.getFavoriteRoute().getId());
    }

    @Override
    public List<FavoriteRouteDestination> getDestinationsByRoute(FavoriteRoute route) {
        return destinationRepository.findByFavoriteRouteIdOrderByDestinationOrder(route.getId());
    }

    @Override
    public List<FavoriteRouteDestination> getDestinationsByRouteId(Long routeId) {
        return destinationRepository.findByFavoriteRouteIdOrderByDestinationOrder(routeId);
    }

    @Override
    @Transactional
    public void reorderDestinations(Long routeId, List<Long> destinationIdsInOrder) {
        for (int i = 0; i < destinationIdsInOrder.size(); i++) {
            Long destinationId = destinationIdsInOrder.get(i);
            FavoriteRouteDestination destination = destinationRepository.findById(destinationId)
                    .orElseThrow(() -> new RuntimeException("Destination not found with id: " + destinationId));

            if (!destination.getFavoriteRoute().getId().equals(routeId)) {
                throw new RuntimeException("Destination does not belong to the specified route");
            }

            destination.setDestinationOrder(i + 1);
            destinationRepository.save(destination);
        }
    }

    @Override
    @Transactional
    public void clearRouteDestinations(Long routeId) {
        destinationRepository.deleteByFavoriteRouteId(routeId);
    }

    @Override
    public int getNextDestinationOrder(Long routeId) {
        Integer maxOrder = destinationRepository.findMaxDestinationOrderByFavoriteRouteId(routeId);
        return maxOrder != null ? maxOrder + 1 : 1;
    }

    private void reorderRemainingDestinations(Long routeId) {
        List<FavoriteRouteDestination> destinations =
                destinationRepository.findByFavoriteRouteIdOrderByDestinationOrder(routeId);

        for (int i = 0; i < destinations.size(); i++) {
            destinations.get(i).setDestinationOrder(i + 1);
            destinationRepository.save(destinations.get(i));
        }
    }
}