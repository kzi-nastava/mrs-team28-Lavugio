package com.backend.lavugio.service.ride.impl;

import com.backend.lavugio.dto.ScheduledRideDTO;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.route.RideDestination;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.ride.ScheduledRideService;
import com.backend.lavugio.service.route.RideDestinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    @Override
    public List<ScheduledRideDTO> getScheduledRidesForDriver(Long driverId){
        List<Ride> rides = rideService.getScheduledRidesForDriver(driverId);
        List<RideDestination> rideDestinations = rideDestinationService.getStartAndEndDestinationForRides(rides.stream().map(Ride::getId).collect(Collectors.toList()));
        List<ScheduledRideDTO> scheduledRides = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
        for (int i  = 0; i < rides.size(); i++) {
            scheduledRides.add(new ScheduledRideDTO(
                    rides.get(i).getId(),
                    rideDestinations.get(i*2).getAddress().toString(),
                    rideDestinations.get(i*2+1).getAddress().toString(),
                    rides.get(i).getStart().format(formatter)
            ));
        }
        return scheduledRides;
    }
}
