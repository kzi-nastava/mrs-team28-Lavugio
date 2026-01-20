package com.backend.lavugio.service.pricing.impl;

import com.backend.lavugio.dto.pricing.KilometerPricingDTO;
import com.backend.lavugio.dto.pricing.VehiclePricingDTO;
import com.backend.lavugio.model.enums.VehicleType;
import com.backend.lavugio.model.pricing.KilometerPricing;
import com.backend.lavugio.model.pricing.VehiclePricing;
import com.backend.lavugio.repository.pricing.KilometerPricingRepository;
import com.backend.lavugio.repository.pricing.VehiclePricingRepository;
import com.backend.lavugio.service.pricing.PricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PricingServiceImpl implements PricingService {

    private final VehiclePricingRepository vehiclePricingRepository;

    private final KilometerPricingRepository kilometerPricingRepository;

    @Autowired
    public PricingServiceImpl(VehiclePricingRepository vehiclePricingRepository,  KilometerPricingRepository kilometerPricingRepository) {
        this.vehiclePricingRepository = vehiclePricingRepository;
        this.kilometerPricingRepository = kilometerPricingRepository;
    }

    @Override
    public Double getVehiclePricingByVehicleType(VehicleType vehicleType) {
        VehiclePricing vehiclePricing = vehiclePricingRepository.getVehiclePricingByVehicleType(vehicleType);
        if (vehiclePricing == null) {
            vehiclePricing = new VehiclePricing();
            vehiclePricing.setVehicleType(vehicleType);
            vehiclePricing.setPricing(200d);
            vehiclePricingRepository.save(vehiclePricing);
            vehiclePricingRepository.flush();
        }
        return vehiclePricing.getPricing();
    }

    @Override
    public void updateVehiclePricing(VehiclePricingDTO vehiclePricingDTO){
        VehiclePricing  vehiclePricing = vehiclePricingRepository.getVehiclePricingByVehicleType(vehiclePricingDTO.getVehicleType());
        if (vehiclePricing == null) {
            vehiclePricing = new VehiclePricing();
        }
        vehiclePricing.setVehicleType(vehiclePricingDTO.getVehicleType());
        vehiclePricing.setPricing(vehiclePricingDTO.getNewPricing());
        vehiclePricingRepository.save(vehiclePricing);
        vehiclePricingRepository.flush();
    }

    @Override
    public Double getKilometerPricing() {
        KilometerPricing  kilometerPricing = kilometerPricingRepository.getKilometerPricing();
        if (kilometerPricing == null) {
            kilometerPricing = new KilometerPricing(1L, 150d);
            kilometerPricingRepository.save(kilometerPricing);
            kilometerPricingRepository.flush();
        }
        return kilometerPricing.getPricePerKilometer();
    }

    @Override
    public void updateKilometerPricing(KilometerPricingDTO kilometerPricingDTO) {
        KilometerPricing kilometerPricing = new KilometerPricing(1L, kilometerPricingDTO.getNewPricing());
        kilometerPricingRepository.save(kilometerPricing);
        kilometerPricingRepository.flush();
    }
}
