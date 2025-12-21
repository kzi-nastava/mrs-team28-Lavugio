package com.backend.lavugio.model.route;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "favorite_route_destinations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRouteDestination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favorite_route_id", nullable = false)
    private FavoriteRoute favoriteRoute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Column(nullable = false)
    private Integer destinationOrder;
}
