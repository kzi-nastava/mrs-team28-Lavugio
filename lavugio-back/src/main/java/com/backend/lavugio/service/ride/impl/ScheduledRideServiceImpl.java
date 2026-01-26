package com.backend.lavugio.service.ride.impl;

import com.backend.lavugio.dto.CoordinatesDTO;
import com.backend.lavugio.dto.ride.ScheduledRideDTO;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.route.RideDestination;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.ride.ScheduledRideService;
import com.backend.lavugio.service.route.RideDestinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduledRideServiceImpl implements ScheduledRideService {

    private final RideService rideService;
    private final RideDestinationService rideDestinationService;

    @Autowired
    public ScheduledRideServiceImpl(RideService rideService, RideDestinationService rideDestinationService) {
        this.rideService = rideService;
        this.rideDestinationService = rideDestinationService;
    }

    private Long rideId;
    private String startAddress;
    private String endAddress;
    private LocalDateTime scheduledTime;
    private CoordinatesDTO[] checkpoints;
    private Float price;
    private RideStatus status;
    private boolean isPanicked;

    @Override
    public List<ScheduledRideDTO> getScheduledRidesForDriver(Long driverId) {
        List<Ride> rides = rideService.getScheduledRidesForDriver(driverId);
        rides = rides.stream()
                .sorted(Comparator.comparing(Ride::getStartDateTime))
                .toList();

        List<ScheduledRideDTO> scheduledRides = new ArrayList<>();

        for (Ride ride : rides) {
            List<RideDestination> destinations = rideDestinationService.getOrderedDestinationsByRideId(ride.getId());

            if (destinations.isEmpty()) {
                continue;
            }

            String startAddress = destinations.getFirst().getAddress().toString();
            String endAddress = destinations.getLast().getAddress().toString();

            List<CoordinatesDTO> checkpoints = destinations.stream()
                    .map(dest -> new CoordinatesDTO(
                            dest.getAddress().getLatitude(),
                            dest.getAddress().getLongitude()
                    ))
                    .toList();

            scheduledRides.add(new ScheduledRideDTO(ride, checkpoints, startAddress, endAddress));
        }

        return scheduledRides;
    }
}
