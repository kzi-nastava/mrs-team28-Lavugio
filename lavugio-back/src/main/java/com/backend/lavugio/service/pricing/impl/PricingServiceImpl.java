package com.backend.lavugio.service.pricing.impl;

import com.backend.lavugio.dto.pricing.PricingDTO;
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
    public void updatePricing(PricingDTO pricingDTO){
        updateVehiclePricing(VehicleType.STANDARD, pricingDTO.getStandard());
        updateVehiclePricing(VehicleType.LUXURY, pricingDTO.getLuxury());
        updateVehiclePricing(VehicleType.COMBI, pricingDTO.getCombi());
        updateKilometerPricing(pricingDTO.getKilometer());
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
    public PricingDTO getPricing(){
        PricingDTO pricingDTO = new PricingDTO();
        pricingDTO.setStandard(this.getVehiclePricingByVehicleType(VehicleType.STANDARD));
        pricingDTO.setLuxury(this.getVehiclePricingByVehicleType(VehicleType.LUXURY));
        pricingDTO.setCombi(this.getVehiclePricingByVehicleType(VehicleType.COMBI));
        pricingDTO.setKilometer(this.getKilometerPricing());
        return pricingDTO;
    }

    private void updateKilometerPricing(Double price) {
        KilometerPricing kilometerPricing = kilometerPricingRepository.getKilometerPricing();
        kilometerPricing.setPricePerKilometer(price);
        kilometerPricingRepository.save(kilometerPricing);
        kilometerPricingRepository.flush();
    }

    private void updateVehiclePricing(VehicleType vehicleType, Double price) {
        VehiclePricing vehiclePricing = vehiclePricingRepository.getVehiclePricingByVehicleType(vehicleType);
        if (vehiclePricing == null) {
            vehiclePricing = new VehiclePricing();
        }
        vehiclePricing.setVehicleType(vehicleType);
        vehiclePricing.setPricing(price);
        vehiclePricingRepository.save(vehiclePricing);
        vehiclePricingRepository.flush();
    }
}
