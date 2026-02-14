package com.backend.lavugio.dto.user;

import com.backend.lavugio.dto.vehicle.VehicleDTO;
import com.backend.lavugio.model.enums.VehicleType;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.vehicle.Vehicle;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DriverDTO extends UserDTO {
    private boolean active;

    private String vehicleLicensePlate;
    private String vehicleMake;
    private String vehicleModel;
    private String vehicleColor;
    private VehicleType vehicleType;
    private boolean vehicleBabyFriendly;
    private boolean vehiclePetFriendly;

    public DriverDTO(Driver driver) {
        this.setId(driver.getId());
        this.setName(driver.getName());
        this.setLastName(driver.getLastName());
        this.setEmail(driver.getEmail());
        this.setPhoneNumber(driver.getPhoneNumber());
        this.setAddress(driver.getAddress());
        this.setProfilePhotoPath(driver.getProfilePhotoPath());
        this.setEmailVerified(driver.isEmailVerified());
        this.setRole("DRIVER");
        
        this.setBlocked(driver.isBlocked());
        this.setBlockReason(driver.getBlockReason());
        
        this.active = driver.isActive();
        
        Vehicle vehicle = driver.getVehicle();
        if (vehicle != null) {
            this.vehicleLicensePlate = vehicle.getLicensePlate();
            this.vehicleMake = vehicle.getMake();
            this.vehicleModel = vehicle.getModel();
            this.vehicleColor = vehicle.getColor();
            this.vehicleType = vehicle.getType();
            this.vehicleBabyFriendly = vehicle.isBabyFriendly();
            this.vehiclePetFriendly = vehicle.isPetFriendly();
        }
    }
}
