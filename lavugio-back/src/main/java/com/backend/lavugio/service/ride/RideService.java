package com.backend.lavugio.service.ride;

import com.backend.lavugio.dto.ride.FinishRideDTO;
import com.backend.lavugio.dto.ride.RideRequestDTO;
import com.backend.lavugio.dto.ride.RideResponseDTO;
import com.backend.lavugio.model.enums.DriverHistorySortFieldEnum;
import com.backend.lavugio.model.enums.VehicleType;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.enums.RideStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface RideService {

    // Create operations
    Ride createRide(Ride ride);

    // Read operations
    Ride getRideById(Long id);
    List<Ride> getAllRides();
    List<Ride> getRidesByDriverId(Long driverId);
    List<Ride> getRidesByPassengerId(Long passengerId);
    List<Ride> getRidesByDate(LocalDateTime date);
    List<Ride> getRidesByStatus(RideStatus status);
    List<Ride> getUpcomingRidesForDriver(Long driverId);
    List<Ride> getRidesInDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<Ride> getActiveRides();
    List<Ride> getScheduledRidesForDriver(Long driverId);
    List<Ride> getFinishedRidesForDriver(Long driverId);

    // Update operations
    Ride updateRide(Long id, Ride ride);
    Ride updateRideStatus(Long id, RideStatus newStatus);
    Ride addPassengerToRide(Long rideId, Long passengerId);
    Ride addPassengerToRide(Ride ride, List<String> passengerEmails);
    Ride removePassengerFromRide(Long rideId, Long passengerId);

    // Delete operations
    void cancelRide(Long id);
    void deleteRide(Long id);

    // Business logic operations
    boolean isRideAvailable(Long rideId);
    Float calculateTotalEarningsForDriver(Long driverId);
    Float calculateTotalDistanceForDriver(Long driverId);
    Float calculateAverageFareForDriver(Long driverId);
    List<Ride> applyParametersToRides(List<Ride> rides, boolean ascending, DriverHistorySortFieldEnum sortBy, String dateRangeStart, String dateRangeEnd);
    Double calculatePrice(VehicleType vehicleType, Double distance);

    // Instant Ride Creation
    RideResponseDTO createInstantRide(String userEmail, RideRequestDTO request);
}