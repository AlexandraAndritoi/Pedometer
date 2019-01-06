package com.chs.pedometer;

import java.util.ArrayList;

public class Route {
    public String day;
    public Double distance;
    public Double speed;
    public int steps;
    public ArrayList<Point> locations;

    public Route(String day, Double distance, Double speed, int steps, ArrayList<Point> locations) {
        this.day = day;
        this.distance = distance;
        this.speed = speed;
        this.steps = steps;
        this.locations = locations;
    }

    public String getDay() {
        return day;
    }

    public Double getDistance() {
        return distance;
    }

    public Double getSpeed() {
        return speed;
    }

    public int getSteps() {
        return steps;
    }

    public ArrayList<Point> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<Point> locations) {
        this.locations = locations;
    }

    public void addLocation (Point location) {
        this.locations.add(location);
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
