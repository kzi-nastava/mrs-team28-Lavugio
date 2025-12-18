package model.route;

import java.util.List;

public class Route {
    private List<Address> route;

    public Route() {

    }

    public Route(List<Address> route) {
        this.route = route;
    }

    public List<Address> getRoute() {
        return route;
    }

    public void setRoute(List<Address> route) {
        this.route = route;
    }
}
