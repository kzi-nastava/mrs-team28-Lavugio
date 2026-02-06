package com.backend.lavugio.service.pricing;

import com.backend.lavugio.dto.pricing.PricingDTO;
import com.backend.lavugio.model.enums.VehicleType;

public interface PricingService {
    Double getKilometerPricing();
    Double getVehiclePricingByVehicleType(VehicleType vehicleType);
    void updatePricing(PricingDTO pricingDTO);
    PricingDTO getPricing();
}
