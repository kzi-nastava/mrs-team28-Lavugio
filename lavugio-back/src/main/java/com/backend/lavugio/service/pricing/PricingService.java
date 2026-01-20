package com.backend.lavugio.service.pricing;

import com.backend.lavugio.dto.pricing.KilometerPricingDTO;
import com.backend.lavugio.dto.pricing.VehiclePricingDTO;
import com.backend.lavugio.model.enums.VehicleType;

public interface PricingService {
    Double getKilometerPricing();
    void updateKilometerPricing(KilometerPricingDTO kilometerPricingDTO);
    Double getVehiclePricingByVehicleType(VehicleType vehicleType);
    void updateVehiclePricing(VehiclePricingDTO vehiclePricingDTO);
}
