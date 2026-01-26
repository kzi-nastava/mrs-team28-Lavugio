package com.backend.lavugio.model.pricing;

import com.backend.lavugio.model.enums.VehicleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="vehicle_pricing")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VehiclePricing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private VehicleType vehicleType;

    @Column
    private Double pricing;
}
