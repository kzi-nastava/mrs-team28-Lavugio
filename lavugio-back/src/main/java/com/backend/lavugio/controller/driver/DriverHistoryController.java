package com.backend.lavugio.controller.driver;

import com.backend.lavugio.dto.*;
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
//        List<Ride> rides = rideService.getFinishedRidesForDriver(driverId);
//        rideService.applyParametersToRides(rides, ascending, sortBy, dateRangeStart, dateRangeEnd);
//        List<DriverHistoryDTO> ridesDTO = new ArrayList<>();
//        for (Ride ride : rides){
//            ridesDTO.add(new DriverHistoryDTO(ride));
//        }
        List<DriverHistoryDTO> rides = new ArrayList<>();
        rides.add(new DriverHistoryDTO(1L, "Location A", "Location B", "11:23 15.03.2024", "12:01 15.03.2024"));
        rides.add(new DriverHistoryDTO(2L, "Location C", "Location D", "09:10 14.03.2024", "09:45 14.03.2024"));
        rides.add(new DriverHistoryDTO(3L, "Location E", "Location F", "14:05 13.03.2024", "14:50 13.03.2024"));
        return new ResponseEntity<>(rides, HttpStatus.OK);
    }

    @GetMapping(value = "/{rideId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverHistoryDetailedDTO> getDriverHistoryByRideId(@PathVariable Long rideId){
//        Ride ride = rideService.getRideById(rideId);
//        List<RideDestination> destinations = rideDestinationService.getStartAndEndDestinationForRide(rideId);
//        RideDestination startDestination = destinations.get(0);
//        RideDestination endDestination = destinations.get(1);
//        DriverHistoryDetailedDTO rideDTO = new DriverHistoryDetailedDTO(ride, startDestination, endDestination);
        List<PassengerTableRowDTO> passengers = new ArrayList<>();
        passengers.add(new PassengerTableRowDTO(
                1L,
                "Marko Marković",
                new ImageDTO("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==", "image/png")
        ));
        passengers.add(new PassengerTableRowDTO(
                2L,
                "Ana Anić",
                new ImageDTO("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8DwHwAFBQIAX8jx0gAAAABJRU5ErkJggg==", "image/png")
        ));
        passengers.add(new PassengerTableRowDTO(
                3L,
                "Petar Petrović",
                new ImageDTO("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChAGA+G6D9wAAAABJRU5ErkJggg==", "image/png")
        ));

        DriverHistoryDetailedDTO dto = new DriverHistoryDetailedDTO(
                "11:23 15.03.2024",
                "12:45 15.03.2024",
                "Kneza Miloša 15, Beograd",
                "Bulevar kralja Aleksandra 73, Beograd",
                1250.50,
                false,
                false,
                passengers,
                new CoordinatesDTO(44.8125, 20.4612),
                new CoordinatesDTO(44.8023, 20.4856)
        );

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

}


