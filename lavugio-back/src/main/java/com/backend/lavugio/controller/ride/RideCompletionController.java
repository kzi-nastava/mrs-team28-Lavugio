package com.backend.lavugio.controller.ride;

import com.backend.lavugio.dto.FinishRideDTO;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.ride.RideStatus;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.service.notification.NotificationService;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.user.DriverService;
import com.backend.lavugio.service.user.RegularUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rides/{rideId}/complete")
public class RideCompletionController {

    private final RideService rideService;
    private final DriverService driverService;
    private final NotificationService notificationService;
    private final RegularUserService userService;

    @Autowired
    private RideCompletionController(RideService rideService,
                                     DriverService driverService,
                                     NotificationService notificationService,
                                     RegularUserService userService) {
        this.rideService = rideService;
        this.driverService = driverService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @PutMapping
    public ResponseEntity<?> completeRide(@PathVariable Long rideId,
                                          @RequestBody FinishRideDTO finishRideDTO) {
//        Ride ride = rideService.getRideById(rideId);
//        Driver driver = ride.getDriver();
//        rideService.updateRideStatus(rideId, RideStatus.FINISHED);
//        driverService.updateDriverDriving(driver.getId(), false);
//        notificationService.notifyPassengersAboutFinishedRide(ride);
//        for (RegularUser user : ride.getPassengers()){
//            userService.enableUserOrdering(user.getId());
//        }
        // notificationService.notifyPassengersAboutCompletedRide();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
