package com.backend.lavugio.controller.user;

import com.backend.lavugio.dto.user.DriverRegistrationRequestDTO;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.vehicle.Vehicle;
import com.backend.lavugio.model.vehicle.VehicleType;
import com.backend.lavugio.service.user.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class RegistrationController
{
    @Autowired
    private DriverService driverService;

    @PostMapping("registration/driver")
    public ResponseEntity<?> registerDriver(@RequestBody DriverRegistrationRequestDTO request)
    {
        try
        {
            Driver driver = new Driver();
            driver.setEmail(request.getEmail());
            driver.setPassword(request.getPassword());
            driver.setName(request.getName());
            driver.setLastName(request.getLastName());

            Vehicle vehicle = new Vehicle();
            vehicle.setLicensePlate(request.getLicensePlate());
            vehicle.setMake(request.getMake());
            vehicle.setModel(request.getModel());
            vehicle.setColor(request.getColor());
            vehicle.setBabyFriendly(request.isBabyFriendly());
            vehicle.setPetFriendly(request.isPetFriendly());
            vehicle.setType(VehicleType.valueOf(request.getVehicleType()));
            driver.setVehicle(vehicle);
            Driver driverCreated = driverService.createDriver(driver);
            return ResponseEntity.status(HttpStatus.CREATED).body("Driver registered successfully");
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
