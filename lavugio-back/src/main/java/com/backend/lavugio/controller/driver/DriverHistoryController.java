package com.backend.lavugio.controller.driver;

import com.backend.lavugio.dto.DriverHistoryDTO;
import com.backend.lavugio.dto.DriverHistoryDetailedDTO;
import com.backend.lavugio.model.enums.DriverHistorySortFieldEnum;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.route.RideDestination;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.route.RideDestinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/drivers/{driverId}/history")
public class DriverHistoryController {

    RideService rideService;
    RideDestinationService rideDestinationService;

    @Autowired
    public DriverHistoryController(RideService rideService, RideDestinationService rideDestinationService) {
        this.rideService = rideService;
        this.rideDestinationService = rideDestinationService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<DriverHistoryDTO>> getAllDriverHistory(@PathVariable Long driverId,
                                                                            @RequestParam(defaultValue = "false") boolean ascending,
                                                                            @RequestParam(defaultValue = "START_DATE") DriverHistorySortFieldEnum sortBy,
                                                                            @RequestParam(required = false) String dateRangeStart,
                                                                            @RequestParam(required = false) String dateRangeEnd) {
        List<Ride> rides = rideService.getFinishedRidesForDriver(driverId);
        rideService.applyParametersToRides(rides, ascending, sortBy, dateRangeStart, dateRangeEnd);
        List<DriverHistoryDTO> ridesDTO = new ArrayList<>();
        for (Ride ride : rides){
            ridesDTO.add(new DriverHistoryDTO(ride));
        }
        return new ResponseEntity<>(ridesDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/{rideId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverHistoryDetailedDTO> getDriverHistoryByRideId(@PathVariable Long rideId){
        Ride ride = rideService.getRideById(rideId);
        List<RideDestination> destinations = rideDestinationService.getStartAndEndDestinationForRide(rideId);
        RideDestination startDestination = destinations.get(0);
        RideDestination endDestination = destinations.get(1);
        DriverHistoryDetailedDTO rideDTO = new DriverHistoryDetailedDTO(ride, startDestination, endDestination);
        return new ResponseEntity<>(rideDTO, HttpStatus.OK);
    }

}


