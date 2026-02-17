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

    public RideDestination getFirstDestination() {
        if (destinations == null || destinations.length == 0) {
            return null;
        }
        return this.destinations[0];
    }

    public RideDestination getLastDestination() {
        if (destinations == null || destinations.length == 0) {
            return null;
        }
        return this.destinations[this.destinations.length - 1];
    }

    public String getId() {
        return String.valueOf(id);
    }

    public String getFromAddress() {
        RideDestination firstDestination = getFirstDestination();
        if (firstDestination != null) {
            return firstDestination.getStreet() + " " + firstDestination.getHouseNumber() + ", " + firstDestination.getCity();
        } else {
            return "";
        }
    }

    public String getToAddress() {
        RideDestination lastDestination = getLastDestination();
        if (lastDestination != null) {
            return lastDestination.getStreet() + " " + lastDestination.getHouseNumber() + ", " + lastDestination.getCity();
        } else {
            return "";
        }
    }

    public int getDestinationsCount() {
        return destinations != null ? destinations.length : 0;
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
