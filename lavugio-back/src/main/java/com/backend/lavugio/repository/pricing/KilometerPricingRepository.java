package com.backend.lavugio.repository.pricing;

import com.backend.lavugio.model.pricing.KilometerPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface KilometerPricingRepository extends JpaRepository<KilometerPricing, Long> {

    @Query("SELECT k FROM KilometerPricing k WHERE k.id = 1")
    KilometerPricing getKilometerPricing();

}
