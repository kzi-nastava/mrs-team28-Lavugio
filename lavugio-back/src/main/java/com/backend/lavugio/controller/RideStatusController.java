package com.backend.lavugio.controller;

import com.backend.lavugio.dto.RideStatusDTO;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.route.Address;
import com.backend.lavugio.model.route.RideDestination;
import com.backend.lavugio.model.route.RouteTimeEstimation;
import com.backend.lavugio.model.user.DriverLocation;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.route.EtaService;
import com.backend.lavugio.service.route.RideDestinationService;
import com.backend.lavugio.service.user.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideStatusController {
    private final EtaService etaService;
    private final RideService rideService;
    private final DriverService driverService;
    private final RideDestinationService rideDestinationService;

    @Autowired
    public RideStatusController(EtaService etaService, RideService rideService, DriverService driverService, RideDestinationService rideDestinationService) {
        this.etaService = etaService;
        this.rideService = rideService;
        this.driverService = driverService;
        this.rideDestinationService = rideDestinationService;
    }

    @GetMapping(value = "/{rideId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RideStatusDTO> getRideStatus(@PathVariable Long rideId) {
        Ride ride = rideService.getRideById(rideId);
        DriverLocation driverLocation = driverService.getDriverStatus(ride.getDriver().getId());
        if (driverLocation == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<RideDestination> rideDestinations = rideDestinationService.getOrderedDestinationsByRideId(rideId);
        Address start = rideDestinations.getFirst().getAddress();
        Address end = rideDestinations.getLast().getAddress();
        RouteTimeEstimation eta;
        try {
            eta = etaService.calculateEta(driverLocation.getLongitude(), driverLocation.getLatitude(), end.getLongitude(), end.getLatitude());
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        RideStatusDTO status =  new RideStatusDTO();
        status.setStartLatitude(start.getLatitude());
        status.setStartLongitude(start.getLongitude());
        status.setDestinationLatitude(end.getLatitude());
        status.setDestinationLongitude(end.getLongitude());
        status.setCurrentLatitude(driverLocation.getLatitude());
        status.setCurrentLongitude(driverLocation.getLongitude());
        status.setRemainingTimeSeconds(eta.getDurationSeconds());
        return new ResponseEntity<>(status, HttpStatus.OK);
    }


}
