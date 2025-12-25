package com.backend.lavugio.service.route.impl;

import com.backend.lavugio.dto.route.DestinationDTO;
import com.backend.lavugio.dto.route.FavoriteRouteDTO;
import com.backend.lavugio.dto.route.FavoriteRouteDestinationDTO;
import com.backend.lavugio.dto.route.UpdateFavoriteRouteDTO;
import com.backend.lavugio.model.route.Address;
import com.backend.lavugio.model.route.FavoriteRoute;
import com.backend.lavugio.model.route.FavoriteRouteDestination;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.repository.route.AddressRepository;
import com.backend.lavugio.repository.route.FavoriteRouteRepository;
import com.backend.lavugio.repository.route.FavoriteRouteDestinationRepository;
import com.backend.lavugio.repository.user.RegularUserRepository;
import com.backend.lavugio.service.route.FavoriteRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteRouteServiceImpl implements FavoriteRouteService {

    @Autowired
    private FavoriteRouteRepository favoriteRouteRepository;

    @Autowired
    private FavoriteRouteDestinationRepository destinationRepository;

    @Autowired
    private RegularUserRepository regularUserRepository;

    @Autowired
    private FavoriteRouteDestinationRepository favoriteRouteDestinationRepository;

    @Autowired
    private AddressRepository addressRepository;

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

    @Override
    public FavoriteRouteDTO createFavoriteRoute(FavoriteRouteDTO request) {
        // Check if user exists
        RegularUser user = regularUserRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        // Create FavoriteRoute
        FavoriteRoute favoriteRoute = new FavoriteRoute();
        favoriteRoute.setName(request.getName());
        favoriteRoute.setUser(user);

        FavoriteRoute savedRoute = favoriteRouteRepository.save(favoriteRoute);

        // Create destinations
        if (request.getDestinations() != null) {
            for (int i = 0; i < request.getDestinations().size(); i++) {
                FavoriteRouteDestinationDTO destDto = request.getDestinations().get(i);

                // Create or find address
                Address address = new Address();
                address.setStreetName(destDto.getStreetName());
                address.setCity(destDto.getCity());
                address.setCountry(destDto.getCountry());
                address.setStreetNumber(destDto.getStreetNumber());
                address.setZipCode(destDto.getZipCode());
                address.setLongitude(destDto.getLongitude());
                address.setLatitude(destDto.getLatitude());

                Address savedAddress = addressRepository.save(address);

                // Create destination
                FavoriteRouteDestination destination = new FavoriteRouteDestination();
                destination.setFavoriteRoute(savedRoute);
                destination.setAddress(savedAddress);
                destination.setDestinationOrder(i + 1);

                favoriteRouteDestinationRepository.save(destination);
            }
        }

        return mapToDTO(savedRoute);
    }

    @Override
    public FavoriteRouteDTO getFavoriteRouteDTOById(Long id) {
        FavoriteRoute favoriteRoute = favoriteRouteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Favorite route not found with id: " + id));
        return mapToDTO(favoriteRoute);
    }

    @Override
    public List<FavoriteRouteDTO> getFavoriteRoutesDTOByUser(Long userId) {
        // Check if user exists
        if (!regularUserRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        List<FavoriteRoute> favoriteRoutes = favoriteRouteRepository.findByUserId(userId);
        return favoriteRoutes.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FavoriteRouteDTO> getAllFavoriteRoutesDTO() {
        List<FavoriteRoute> favoriteRoutes = favoriteRouteRepository.findAll();
        return favoriteRoutes.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FavoriteRouteDTO updateFavoriteRouteDTO(Long id, UpdateFavoriteRouteDTO request) {
        FavoriteRoute favoriteRoute = favoriteRouteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Favorite route not found with id: " + id));

        // Update name
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            favoriteRoute.setName(request.getName());
        }

        // Update destinations if provided
        if (request.getDestinations() != null) {
            // Delete existing destinations
            List<FavoriteRouteDestination> existingDestinations =
                    favoriteRouteDestinationRepository.findByFavoriteRouteId(id);
            favoriteRouteDestinationRepository.deleteAll(existingDestinations);

            // Create new destinations
            for (int i = 0; i < request.getDestinations().size(); i++) {
                DestinationDTO destDto = request.getDestinations().get(i);

                // Create address
                Address address = new Address();
                address.setStreetName(destDto.getStreetName());
                address.setCity(destDto.getCity());
                address.setCountry(destDto.getCountry());
                address.setStreetNumber(destDto.getStreetNumber());
                address.setZipCode(destDto.getZipCode());
                address.setLongitude(destDto.getLongitude());
                address.setLatitude(destDto.getLatitude());

                Address savedAddress = addressRepository.save(address);

                // Create destination
                FavoriteRouteDestination destination = new FavoriteRouteDestination();
                destination.setFavoriteRoute(favoriteRoute);
                destination.setAddress(savedAddress);
                destination.setDestinationOrder(destDto.getOrder() != null ? destDto.getOrder() : i + 1);

                favoriteRouteDestinationRepository.save(destination);
            }
        }

        FavoriteRoute updatedRoute = favoriteRouteRepository.save(favoriteRoute);
        return mapToDTO(updatedRoute);
    }

    @Override
    public void deleteFavoriteRoute(Long id) {
        if (!favoriteRouteRepository.existsById(id)) {
            throw new RuntimeException("Favorite route not found with id: " + id);
        }

        // Delete destinations first
        List<FavoriteRouteDestination> destinations =
                favoriteRouteDestinationRepository.findByFavoriteRouteId(id);
        favoriteRouteDestinationRepository.deleteAll(destinations);

        // Delete the route
        favoriteRouteRepository.deleteById(id);
    }

    @Override
    public void deleteAllFavoriteRoutesByUser(Long userId) {
        // Check if user exists
        if (!regularUserRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        // Get all favorite routes for user
        List<FavoriteRoute> favoriteRoutes = favoriteRouteRepository.findByUserId(userId);

        // Delete destinations for each route
        for (FavoriteRoute route : favoriteRoutes) {
            List<FavoriteRouteDestination> destinations =
                    favoriteRouteDestinationRepository.findByFavoriteRouteId(route.getId());
            favoriteRouteDestinationRepository.deleteAll(destinations);
        }

        // Delete all routes
        favoriteRouteRepository.deleteAll(favoriteRoutes);
    }

    // Helper method to map entity to DTO
    private FavoriteRouteDTO mapToDTO(FavoriteRoute favoriteRoute) {
        FavoriteRouteDTO dto = new FavoriteRouteDTO();
        dto.setId(favoriteRoute.getId());
        dto.setName(favoriteRoute.getName());
        dto.setUserId(favoriteRoute.getUser().getId());
        dto.setUserName(favoriteRoute.getUser().getName() + " " + favoriteRoute.getUser().getLastName());

        // Get destinations
        List<FavoriteRouteDestination> destinations =
                favoriteRouteDestinationRepository.findByFavoriteRouteId(favoriteRoute.getId());

        List<FavoriteRouteDestinationDTO> destinationDTOs = destinations.stream()
                .map(this::mapDestinationToDTO)
                .collect(Collectors.toList());

        dto.setDestinations(destinationDTOs);
        return dto;
    }

    private FavoriteRouteDestinationDTO mapDestinationToDTO(FavoriteRouteDestination destination) {
        FavoriteRouteDestinationDTO dto = new FavoriteRouteDestinationDTO();
        dto.setId(destination.getId());

        Address address = destination.getAddress();
        dto.setAddressId(address.getId());
        dto.setStreetName(address.getStreetName());
        dto.setCity(address.getCity());
        dto.setCountry(address.getCountry());
        dto.setStreetNumber(address.getStreetNumber());
        dto.setZipCode(address.getZipCode());
        dto.setLongitude(address.getLongitude());
        dto.setLatitude(address.getLatitude());
        dto.setDestinationOrder(destination.getDestinationOrder());

        return dto;
    }

}