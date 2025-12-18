package model.route;

public class FavoriteRoute {
    private long id;
    private String name;
    private Route route;

    public FavoriteRoute() {

    }

    public FavoriteRoute(String name, Route route) {
        this.name = name;
        this.route = route;
    }

    public FavoriteRoute(long id, String name, Route route) {
        this.id = id;
        this.name = name;
        this.route = route;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }
}
