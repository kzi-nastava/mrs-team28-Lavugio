package com.backend.lavugio.controller;

import com.backend.lavugio.model.user.DriverLocation;
import com.backend.lavugio.service.user.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/drivers")
public class DriverStatusController {

    private final DriverService driverService;

    @Autowired
    public DriverStatusController(DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping(value = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<Long, DriverLocation>> getDriverStatuses(){
        Map<Long, DriverLocation> statuses = driverService.getAllActiveDriverStatuses();
        return new ResponseEntity<>(statuses, HttpStatus.OK);
    }

    @GetMapping(value = "/{driverId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverLocation> getDriverStatus(@PathVariable Long driverId){
        DriverLocation driverLocation = driverService.getDriverStatus(driverId);
        if (driverLocation == null){
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(driverLocation, HttpStatus.OK);
    }

    @PostMapping(value = "/{driverId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverLocation> activateDriver(@PathVariable Long driverId,
                                                         @RequestParam double longitude,
                                                         @RequestParam double latitude) {
        DriverLocation status;
        try{
            status = driverService.activateDriver(driverId, longitude, latitude);
        }catch (RuntimeException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(status, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{driverId}/location", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverLocation> updateDriverLocation(@PathVariable Long driverId,
                                                               @RequestParam double longitude,
                                                               @RequestParam double latitude) {
        DriverLocation status;
        try{
            status = driverService.updateDriverLocation(driverId, longitude, latitude);
        } catch (RuntimeException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PutMapping(value = "/{driverId}/driving", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateDriverDriving(@PathVariable Long driverId, @RequestParam boolean isDriving) {
        try{
            driverService.updateDriverDriving(driverId, isDriving);
        } catch (RuntimeException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/{driverId}")
    public ResponseEntity<?> deactivateDriver(@PathVariable Long driverId) {
        driverService.deactivateDriver(driverId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
