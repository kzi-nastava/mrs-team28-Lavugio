package com.backend.lavugio.model.pricing;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "kilometer_pricing" )
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KilometerPricing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 1L;

    @Column
    private Double pricePerKilometer;
}
