package com.backend.lavugio.service.ride;

import com.backend.lavugio.dto.ride.*;
import com.backend.lavugio.dto.ride.FinishRideDTO;
import com.backend.lavugio.dto.user.AdminHistoryDetailedDTO;
import com.backend.lavugio.dto.user.AdminHistoryPagingDTO;
import com.backend.lavugio.dto.user.DriverHistoryDetailedDTO;
import com.backend.lavugio.dto.user.DriverHistoryPagingDTO;
import com.backend.lavugio.dto.user.UserHistoryDetailedDTO;
import com.backend.lavugio.dto.user.UserHistoryPagingDTO;
import com.backend.lavugio.model.enums.DriverHistorySortFieldEnum;
import com.backend.lavugio.model.enums.VehicleType;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.enums.RideStatus;

import java.awt.print.Pageable;
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
    List<Ride> getRidesByCreatorAndStatus(Long creatorId, RideStatus status);
    List<Ride> getRidesByDate(LocalDateTime date);
    List<Ride> getRidesByStatus(RideStatus status);
    List<Ride> getUpcomingRidesForDriver(Long driverId);
    List<Ride> getRidesInDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<Ride> getActiveRides();
    List<Ride> getScheduledRidesForDriver(Long driverId);
    List<Ride> getFinishedRidesForDriver(Long driverId);
    List<Ride> getFinishedRidesInDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<Ride> getFinishedRidesForDriverInDateRange(Long driverId, LocalDateTime startDate, LocalDateTime endDate);
    List<Ride> getFinishedRidesForCreatorInDateRange(Long creatorId, LocalDateTime startDate, LocalDateTime endDate);

    // Update operations
    Ride updateRide(Long id, Ride ride);
    Ride updateRideStatus(Long id, RideStatus newStatus);
    void markRideWithPanic(Long rideId);
    Ride addPassengerToRide(Long rideId, Long passengerId);
    Ride addPassengersToRide(Ride ride, List<String> passengerEmails);
    Ride removePassengerFromRide(Long rideId, Long passengerId);

    // Delete operations
    void cancelRide(Long id);
    void cancelRideByDriver(Long rideId, String reason);
    void cancelRideByPassenger(Long rideId);
    void deleteRide(Long id);

    // Business logic operations
    boolean isRideAvailable(Long rideId);
    Float calculateTotalEarningsForDriver(Long driverId);
    Float calculateTotalDistanceForDriver(Long driverId);
    Float calculateAverageFareForDriver(Long driverId);
    List<Ride> applyParametersToRides(List<Ride> rides, boolean ascending, DriverHistorySortFieldEnum sortBy, String dateRangeStart, String dateRangeEnd);
    double estimateRidePrice(RideEstimateRequestDTO request);
    Double calculatePrice(VehicleType vehicleType, Double distance);
    DriverHistoryPagingDTO getDriverHistory(Long driverId, LocalDateTime startDate, LocalDateTime endDate, String sortBy, String sorting, int pageSize, int pageNumber);
    DriverHistoryDetailedDTO getDriverHistoryDetailed(Long driverId, Long rideId);
    
    // User (passenger) history
    UserHistoryPagingDTO getUserHistory(Long userId, LocalDateTime startDate, LocalDateTime endDate, String sortBy, String sorting, int pageSize, int pageNumber);
    UserHistoryDetailedDTO getUserHistoryDetailed(Long userId, Long rideId);
    
    // Admin: View any user's history by email
    AdminHistoryPagingDTO getAdminHistory(String email, LocalDateTime startDate, LocalDateTime endDate, String sortBy, String sorting, int pageSize, int pageNumber);
    AdminHistoryDetailedDTO getAdminHistoryDetailed(Long rideId);
    
    // Instant Ride Creation
    RideResponseDTO createInstantRide(Long creatorID, RideRequestDTO request);
    RideResponseDTO createScheduledRide(Long creatorID, RideRequestDTO request);
    LatestRideDTO getLatestRide(Long userId);

    void startRide(Long rideId);
}