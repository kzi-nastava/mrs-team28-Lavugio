package com.backend.lavugio.model.route;

import jakarta.persistence.*;

@Entity
@Table(name = "favorite_route_destinations")
public class FavoriteRouteDestination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favorite_route_id", nullable = false)
    private FavoriteRoute favoriteRoute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Column(nullable = false)
    private Integer destinationOrder;
}
