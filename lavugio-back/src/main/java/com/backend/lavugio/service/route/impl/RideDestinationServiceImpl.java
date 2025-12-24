package com.backend.lavugio.service.route.impl;

import com.backend.lavugio.dto.ScheduledRideDTO;
import com.backend.lavugio.model.route.RideDestination;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.route.Address;
import com.backend.lavugio.repository.route.RideDestinationRepository;
import com.backend.lavugio.repository.ride.RideRepository;
import com.backend.lavugio.repository.route.AddressRepository;
import com.backend.lavugio.service.route.RideDestinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RideDestinationServiceImpl implements RideDestinationService {

    @Autowired
    private RideDestinationRepository rideDestinationRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Override
    @Transactional
    public RideDestination addDestinationToRide(RideDestination rideDestination) {
        // Provera da li vožnja postoji
        Ride ride = rideRepository.findById(rideDestination.getRide().getId())
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // Provera da li adresa postoji
        Address address = addressRepository.findById(rideDestination.getAddress().getId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // Ako nije naveden redosled, pronađi sledeći
        if (rideDestination.getDestinationOrder() == null) {
            List<RideDestination> existingDestinations =
                    rideDestinationRepository.findByRideIdOrderByDestinationOrder(ride.getId());
            rideDestination.setDestinationOrder(existingDestinations.size() + 1);
        }

        rideDestination.setRide(ride);
        rideDestination.setAddress(address);

        return rideDestinationRepository.save(rideDestination);
    }

    @Override
    @Transactional
    public void removeDestination(Long destinationId) {
        RideDestination destination = rideDestinationRepository.findById(destinationId)
                .orElseThrow(() -> new RuntimeException("Ride destination not found"));

        rideDestinationRepository.delete(destination);

        // Ponovo poredaj preostale destinacije
        reorderRemainingDestinations(destination.getRide().getId());
    }

    @Override
    public List<RideDestination> getDestinationsByRide(Ride ride) {
        return rideDestinationRepository.findByRideIdOrderByDestinationOrder(ride.getId());
    }

    @Override
    public List<RideDestination> getOrderedDestinationsByRideId(Long rideId) {
        return rideDestinationRepository.findByRideIdOrderByDestinationOrder(rideId);
    }

    @Override
    public List<RideDestination> getStartAndEndDestinationForRides(List<Long> rideIds) {
        return rideDestinationRepository.findFirstAndLastDestinationPerRide(rideIds);
    }

    @Override
    public List<RideDestination> getStartAndEndDestinationForRide(Long rideId) {
        return rideDestinationRepository.findFirstAndLastDestinationForRide(rideId;
    }

    @Override
    public List<Address> getAddressesByRideId(Long rideId) {
        return rideDestinationRepository.findAddressesByRideId(rideId);
    }

    @Override
    @Transactional
    public void reorderDestinations(Long rideId, List<Long> destinationIdsInOrder) {
        for (int i = 0; i < destinationIdsInOrder.size(); i++) {
            Long destinationId = destinationIdsInOrder.get(i);
            RideDestination destination = rideDestinationRepository.findById(destinationId)
                    .orElseThrow(() -> new RuntimeException("Ride destination not found with id: " + destinationId));

            if (!destination.getRide().getId().equals(rideId)) {
                throw new RuntimeException("Destination does not belong to the specified ride");
            }

            destination.setDestinationOrder(i + 1);
            rideDestinationRepository.save(destination);
        }
    }

    @Override
    @Transactional
    public void clearRideDestinations(Long rideId) {
        rideDestinationRepository.deleteByRideId(rideId);
    }

    @Override
    public int getDestinationCountForRide(Long rideId) {
        return rideDestinationRepository.countByRideId(rideId);
    }

    @Override
    public List<String> getAllCitiesWithDestinations() {
        return rideDestinationRepository.findAllCitiesWithDestinations();
    }

    private void reorderRemainingDestinations(Long rideId) {
        List<RideDestination> destinations =
                rideDestinationRepository.findByRideIdOrderByDestinationOrder(rideId);

        for (int i = 0; i < destinations.size(); i++) {
            destinations.get(i).setDestinationOrder(i + 1);
            rideDestinationRepository.save(destinations.get(i));
        }
    }
}