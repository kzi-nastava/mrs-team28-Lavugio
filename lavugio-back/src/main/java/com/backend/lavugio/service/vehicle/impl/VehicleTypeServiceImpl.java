package com.backend.lavugio.service.vehicle.impl;

import com.backend.lavugio.model.enums.VehicleType;
import com.backend.lavugio.service.vehicle.VehicleTypeService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class VehicleTypeServiceImpl implements VehicleTypeService {

    @Override
    public List<VehicleType> getAllVehicleTypes() {
        return Arrays.asList(VehicleType.values());
    }

    @Override
    public boolean isValidVehicleType(String type) {
        try {
            VehicleType.valueOf(type.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}