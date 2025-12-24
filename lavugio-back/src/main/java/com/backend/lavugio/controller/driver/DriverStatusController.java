package com.backend.lavugio.controller.driver;

import com.backend.lavugio.dto.CoordinatesDTO;
import com.backend.lavugio.dto.DriverStatusDTO;
import com.backend.lavugio.dto.UpdateDriverStatusDTO;
import com.backend.lavugio.model.user.DriverLocation;
import com.backend.lavugio.service.user.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    public ResponseEntity<Collection<DriverStatusDTO>> getDriverStatuses(){
        //Map<Long, DriverLocation> statuses = driverService.getAllActiveDriverStatuses();
        List<DriverStatusDTO> statuses = new ArrayList<>();
        statuses.add(new DriverStatusDTO(1L, new CoordinatesDTO(45.2671, 19.8335), true));
        statuses.add(new DriverStatusDTO(2L, new CoordinatesDTO(44.7866, 20.4489), false));
        statuses.add(new DriverStatusDTO(3L, new CoordinatesDTO(43.8563, 18.4131), true));
        return new ResponseEntity<>(statuses, HttpStatus.OK);
    }

    @GetMapping(value = "/{driverId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverStatusDTO> getDriverStatus(@PathVariable Long driverId){
//        DriverLocation driverLocation = driverService.getDriverStatus(driverId);
//        if (driverLocation == null){
//            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
        DriverStatusDTO driverLocation = new DriverStatusDTO(driverId, new CoordinatesDTO(45.2671, 19.8335), true);
        return new ResponseEntity<>(driverLocation, HttpStatus.OK);
    }

    @PostMapping(value = "/{driverId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverStatusDTO> activateDriver(@PathVariable Long driverId,
                                                         @RequestBody CoordinatesDTO coordinates) {
//        DriverLocation status;
//        try{
//            status = driverService.activateDriver(driverId, longitude, latitude);
//        }catch (RuntimeException e){
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
        DriverStatusDTO activatedStatus = new DriverStatusDTO(driverId, coordinates, true);
        return new ResponseEntity<>(activatedStatus, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{driverId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverStatusDTO> updateDriver(@PathVariable Long driverId,
                                                       @RequestBody UpdateDriverStatusDTO driverStatusDTO) {
//        DriverLocation status;
//        try{
//            status = driverService.updateDriverLocation(driverId, longitude, latitude);
//        } catch (RuntimeException e){
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
        DriverStatusDTO updatedStatus = new DriverStatusDTO(driverId,
                driverStatusDTO.getDriverLocation(),
                true);
        return new ResponseEntity<>(updatedStatus, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{driverId}/status")
    public ResponseEntity<?> deactivateDriver(@PathVariable Long driverId) {
        //driverService.deactivateDriver(driverId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
