package com.backend.lavugio.service.vehicle;

import com.backend.lavugio.model.enums.VehicleType;

import java.util.List;

public interface VehicleTypeService {
    List<VehicleType> getAllVehicleTypes();
    boolean isValidVehicleType(String type);
}