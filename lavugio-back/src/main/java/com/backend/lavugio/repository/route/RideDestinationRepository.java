package com.backend.lavugio.repository.route;

import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.route.RideDestination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideDestinationRepository extends JpaRepository<RideDestination, Long> {
    List<RideDestination> findByRide(Ride ride);

    List<RideDestination> findByRideId(Long rideId);

    @Query("SELECT rd FROM RideDestination rd " +
            "WHERE rd.ride.id = :rideId " +
            "ORDER BY rd.destinationOrder ASC")
    List<RideDestination> findByRideIdOrderByDestinationOrder(Long rideId);

    @Query("SELECT rd.address FROM RideDestination rd " +
            "WHERE rd.ride.id = :rideId " +
            "ORDER BY rd.destinationOrder ASC")
    List<com.backend.lavugio.model.route.Address> findAddressesByRideId(Long rideId);

    void deleteByRideId(Long rideId);

    @Query("SELECT COUNT(rd) FROM RideDestination rd WHERE rd.ride.id = :rideId")
    int countByRideId(Long rideId);

    @Query("SELECT rd FROM RideDestination rd WHERE rd.address.city = :city")
    List<RideDestination> findByCity(String city);

    @Query("SELECT DISTINCT rd.address.city FROM RideDestination rd")
    List<String> findAllCitiesWithDestinations();
}