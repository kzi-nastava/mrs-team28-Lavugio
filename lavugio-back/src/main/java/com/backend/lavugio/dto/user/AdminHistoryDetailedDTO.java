package com.backend.lavugio.dto.user;

import com.backend.lavugio.dto.CoordinatesDTO;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.route.RideDestination;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminHistoryDetailedDTO {
    private Long rideId;
    private String start;
    private String end;
    private String departure;
    private String destination;
    private double price;
    private boolean cancelled;
    private String cancelledBy;
    private boolean panic;
    
    // Driver info
    private Long driverId;
    private String driverName;
    private String driverLastName;
    private String driverPhotoPath;
    private String driverPhoneNumber;
    private String driverEmail;
    private String vehicleMake;
    private String vehicleModel;
    private String vehicleLicensePlate;
    private String vehicleColor;
    
    // Passengers
    private List<PassengerInfoDTO> passengers = new ArrayList<>();
    
    // Review info
    private Integer driverRating;
    private Integer carRating;
    private String reviewComment;
    private boolean hasReview;
    
    // Reports
    private List<ReportInfoDTO> reports = new ArrayList<>();
    
    // Checkpoints for map
    private CoordinatesDTO[] checkpoints;
    
    // Full destination info for reordering
    private List<DestinationDetailDTO> destinations = new ArrayList<>();

    public AdminHistoryDetailedDTO(Ride ride) {
        this.rideId = ride.getId();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
        this.start = ride.getStartDateTime().format(formatter);
        this.end = ride.getEndDateTime() != null ? ride.getEndDateTime().format(formatter) : "not finished";
        this.departure = ride.getStartAddress();
        this.destination = ride.getEndAddress();
        this.price = ride.getPrice();
        this.cancelled = ride.getRideStatus() == RideStatus.CANCELLED;
        this.cancelledBy = null; // Not tracked in current model
        this.panic = ride.isHasPanic();
        
        // Driver info
        if (ride.getDriver() != null) {
            this.driverId = ride.getDriver().getId();
            this.driverName = ride.getDriver().getName();
            this.driverLastName = ride.getDriver().getLastName();
            this.driverPhotoPath = ride.getDriver().getProfilePhotoPath();
            this.driverPhoneNumber = ride.getDriver().getPhoneNumber();
            this.driverEmail = ride.getDriver().getEmail();
            
            if (ride.getDriver().getVehicle() != null) {
                this.vehicleMake = ride.getDriver().getVehicle().getMake();
                this.vehicleModel = ride.getDriver().getVehicle().getModel();
                this.vehicleLicensePlate = ride.getDriver().getVehicle().getLicensePlate();
                this.vehicleColor = ride.getDriver().getVehicle().getColor();
            }
        }
        
        // Passengers
        if (ride.getPassengers() != null) {
            this.passengers = ride.getPassengers().stream()
                    .map(p -> new PassengerInfoDTO(p.getId(), p.getName(), p.getLastName(), p.getEmail()))
                    .toList();
        }
        
        // Checkpoints for map and destinations for reordering
        this.checkpoints = ride.getCheckpoints().stream()
                .sorted(Comparator.comparing(RideDestination::getDestinationOrder))
                .map(checkpoint -> new CoordinatesDTO(
                        checkpoint.getAddress().getLatitude(),
                        checkpoint.getAddress().getLongitude()
                ))
                .toArray(CoordinatesDTO[]::new);
        
        // Full destination details for reordering
        this.destinations = ride.getCheckpoints().stream()
                .sorted(Comparator.comparing(RideDestination::getDestinationOrder))
                .map(checkpoint -> new DestinationDetailDTO(
                        checkpoint.getDestinationOrder(),
                        checkpoint.getAddress().getLatitude(),
                        checkpoint.getAddress().getLongitude(),
                        checkpoint.getAddress().toString(),
                        checkpoint.getAddress().getStreetName(),
                        checkpoint.getAddress().getCity(),
                        checkpoint.getAddress().getCountry(),
                        checkpoint.getAddress().getStreetNumber(),
                        checkpoint.getAddress().getZipCode()
                ))
                .toList();
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PassengerInfoDTO {
        private Long passengerId;
        private String passengerName;
        private String passengerLastName;
        private String passengerEmail;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DestinationDetailDTO {
        private int orderIndex;
        private double latitude;
        private double longitude;
        private String address;
        private String streetName;
        private String city;
        private String country;
        private String streetNumber;
        private int zipCode;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportInfoDTO {
        private Long reportId;
        private String reportMessage;
        private String reporterName;
    }
}
