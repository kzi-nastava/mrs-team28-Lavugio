package com.backend.lavugio.service.route;

import com.backend.lavugio.model.route.RideDestination;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.route.Address;

import java.util.List;

public interface RideDestinationService {
    RideDestination addDestinationToRide(RideDestination rideDestination);
    void removeDestination(Long destinationId);
    List<RideDestination> getDestinationsByRide(Ride ride);
    List<RideDestination> getDestinationsByRideId(Long rideId);
    List<Address> getAddressesByRideId(Long rideId);
    void reorderDestinations(Long rideId, List<Long> destinationIdsInOrder);
    void clearRideDestinations(Long rideId);
    int getDestinationCountForRide(Long rideId);
    List<String> getAllCitiesWithDestinations();
}