package com.backend.lavugio.controller;

import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.ride.RideStatus;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.service.notification.NotificationService;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.user.DriverService;
import com.backend.lavugio.service.user.RegularUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rides/{rideId}/finish")
public class RideFinishController {

    private final RideService rideService;
    private final DriverService driverService;
    private final NotificationService notificationService;
    private final RegularUserService userService;

    @Autowired
    private RideFinishController(RideService rideService,
                                 DriverService driverService,
                                 NotificationService notificationService,
                                 RegularUserService userService) {
        this.rideService = rideService;
        this.driverService = driverService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @PostMapping
    public void finishRide(@PathVariable Long rideId){
        Ride ride = rideService.getRideById(rideId);
        Driver driver = ride.getDriver();
        rideService.updateRideStatus(rideId, RideStatus.FINISHED);
        driverService.updateDriverDriving(driver.getId(), false);
        notificationService.notifyPassengersAboutFinishedRide(ride);
        for (RegularUser user : ride.getPassengers()){
            userService.enableUserOrdering(user.getId());
        }
    }
}
