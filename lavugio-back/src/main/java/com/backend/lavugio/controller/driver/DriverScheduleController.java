package com.backend.lavugio.controller.driver;

import com.backend.lavugio.dto.ScheduledRideDTO;
import com.backend.lavugio.service.ride.ScheduledRideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/drivers/{driverId}")
public class DriverScheduleController {

    private final ScheduledRideService scheduledRideService;

    @Autowired
    public DriverScheduleController(ScheduledRideService scheduledRideService) {
        this.scheduledRideService = scheduledRideService;
    }

    @GetMapping(value = "/scheduled-rides", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ScheduledRideDTO>> getAllScheduledRides(@PathVariable Long driverId){
        //return new ResponseEntity<>(scheduledRideService.getScheduledRidesForDriver(driverId),HttpStatus.OK);
        List<ScheduledRideDTO> scheduledRides = new ArrayList<>();
        scheduledRides.add(new ScheduledRideDTO(1L, "Location A", "Location B", "10:50 21.02.2025."));
        scheduledRides.add(new ScheduledRideDTO(2L, "Location C", "Location D", "14:30 22.02.2025."));
        scheduledRides.add(new ScheduledRideDTO(3L, "Location E", "Location F", "09:15 23.02.2025."));
        return new ResponseEntity<>(scheduledRides,HttpStatus.OK);
    }
}
