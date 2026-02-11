package com.example.lavugio_mobile.data.model.route;

public class FavoriteRoute {
    private Long id;
    private String name;

    private RideDestination[] destinations;

    public FavoriteRoute() {
    }

    public FavoriteRoute(Long id, String name, RideDestination[] destinations) {
        this.id = id;
        this.name = name;
        this.destinations = destinations;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RideDestination[] getDestinations() {
        return destinations;
    }

    public void setDestinations(RideDestination[] destinations) {
        this.destinations = destinations;
    }
}
