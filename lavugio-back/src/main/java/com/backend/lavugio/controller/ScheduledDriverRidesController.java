package com.backend.lavugio.controller;

import com.backend.lavugio.dto.ScheduledRideDTO;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.ride.ScheduledRideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;
import java.util.Collection;

@RestController
@RequestMapping("/api/{driverId}")
public class ScheduledDriverRidesController {

    private final ScheduledRideService scheduledRideService;

    @Autowired
    public ScheduledDriverRidesController(ScheduledRideService scheduledRideService) {
        this.scheduledRideService = scheduledRideService;
    }

    @GetMapping(value = "/scheduled-rides", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ScheduledRideDTO>> getAllScheduledRides(@PathVariable Long driverId){
        return new ResponseEntity<>(scheduledRideService.getScheduledRidesForDriver(driverId),HttpStatus.OK);
    }
}
