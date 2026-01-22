package com.backend.lavugio.repository.pricing;

import com.backend.lavugio.model.enums.VehicleType;
import com.backend.lavugio.model.pricing.VehiclePricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehiclePricingRepository extends JpaRepository<VehiclePricing, Long> {
    VehiclePricing getVehiclePricingByVehicleType(VehicleType vehicleType);
}
