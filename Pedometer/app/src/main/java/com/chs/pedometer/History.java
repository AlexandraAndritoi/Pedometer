package com.chs.pedometer;

import java.util.ArrayList;

public class History {
    public ArrayList<Route> routes;

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<Route> routes) {
        this.routes = routes;
    }

    public void replaceRoute(Route newRoute, int index){
        this.routes.add(index, newRoute);
    }
}
