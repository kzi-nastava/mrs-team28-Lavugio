package com.backend.lavugio.controller;

import com.backend.lavugio.model.user.DriverStatus;
import com.backend.lavugio.service.user.ActiveDriverStatusService;
import com.backend.lavugio.service.user.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/driver-statuses")
public class DriverStatusesController {

    private final DriverService driverService;

    @Autowired
    public DriverStatusesController(DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<Long, DriverStatus>> getDriverStatuses(){
        Map<Long, DriverStatus> statuses = driverService.getAllActiveDriverStatuses();
        return new ResponseEntity<>(statuses, HttpStatus.OK);
    }

    @GetMapping(value = "/{driverId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverStatus> getDriverStatus(@PathVariable Long driverId){
        DriverStatus driverStatus = driverService.getDriverStatus(driverId);
        if (driverStatus == null){
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(driverStatus, HttpStatus.OK);
    }

    @PostMapping(value = "/{driverId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverStatus> activateDriver(@PathVariable Long driverId,
                                                       @RequestParam double longitude,
                                                       @RequestParam double latitude) {
        DriverStatus status;
        try{
            status = driverService.activateDriver(driverId, longitude, latitude);
        }catch (RuntimeException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(status, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{driverId}/location", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverStatus> updateDriverLocation(@PathVariable Long driverId,
                                                             @RequestParam double longitude,
                                                             @RequestParam double latitude) {
        DriverStatus status;
        try{
            status = driverService.updateDriverLocation(driverId, longitude, latitude);
        } catch (RuntimeException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PutMapping(value = "/{driverId}/availability", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverStatus> updateDriverAvailability(@PathVariable Long driverId, @RequestParam boolean isAvailable) {
        DriverStatus status;
        try{
            status = driverService.updateDriverAvailability(driverId, isAvailable);
        } catch (RuntimeException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{driverId}")
    public ResponseEntity<?> deactivateDriver(@PathVariable Long driverId) {
        driverService.deactivateDriver(driverId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
