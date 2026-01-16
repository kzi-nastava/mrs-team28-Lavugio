package com.backend.lavugio.repository.route;

import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.route.RideDestination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("""
        SELECT rd
        FROM RideDestination rd
        WHERE rd.ride.id IN :rideIds
          AND (
            rd.destinationOrder = (
                SELECT MIN(rd1.destinationOrder)
                FROM RideDestination rd1
                WHERE rd1.ride = rd.ride
            )
            OR
            rd.destinationOrder = (
                SELECT MAX(rd2.destinationOrder)
                FROM RideDestination rd2
                WHERE rd2.ride = rd.ride
            )
          )
        ORDER BY rd.ride.id, rd.destinationOrder
    """)
    List<RideDestination> findFirstAndLastDestinationPerRide(@Param("rideIds") List<Long> rideIds);

    @Query("""
    SELECT rd
    FROM RideDestination rd
    WHERE rd.ride.id = :rideId
      AND (
        rd.destinationOrder = (
            SELECT MIN(rd1.destinationOrder)
            FROM RideDestination rd1
            WHERE rd1.ride.id = :rideId
        )
        OR
        rd.destinationOrder = (
            SELECT MAX(rd2.destinationOrder)
            FROM RideDestination rd2
            WHERE rd2.ride.id = :rideId
        )
      )
    ORDER BY rd.destinationOrder
""")
    List<RideDestination> findFirstAndLastDestinationForRide(@Param("rideId") Long rideId);
}