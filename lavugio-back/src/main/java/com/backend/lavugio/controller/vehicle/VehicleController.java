package com.backend.lavugio.controller.vehicle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.lavugio.dto.vehicle.UpdateVehicleDTO;
import com.backend.lavugio.dto.vehicle.VehicleDTO;
import com.backend.lavugio.service.vehicle.VehicleService;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

	@Autowired
	private VehicleService vehicleService;
	
	@GetMapping("/driver/{id}")
    public ResponseEntity<?> getDriverVehicle(@PathVariable Long id) {
        try {
            VehicleDTO vehicle = vehicleService.getDriverVehicle(id);
            return ResponseEntity.ok(vehicle);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
	
	@GetMapping("/driver/{id}")
    public ResponseEntity<?> getVehicle(@PathVariable Long id) {
        try {
            VehicleDTO vehicle = vehicleService.getDriverVehicle(id);
            return ResponseEntity.ok(vehicle);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/vehicle")
    public ResponseEntity<?> updateDriverVehicle(
            @PathVariable Long id,
            @RequestBody UpdateVehicleDTO request) {
    	// @AuthenticationPrincipal UserDetails userDetails
        try {
            VehicleDTO vehicle = vehicleService.updateDriverVehicle(id, request, driverEmail);
            return ResponseEntity.ok(vehicle);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
	
}
