package com.chs.pedometer;

import java.util.ArrayList;

public class Route {
    public String day;
    public int distance;
    public Double speed;
    public int steps;
    public ArrayList<Point> locations;
    public long time;

    public Route(String day, int distance, Double speed, int steps, long time, ArrayList<Point> locations) {
        this.day = day;
        this.distance = distance;
        this.speed = speed;
        this.steps = steps;
        this.locations = locations;
        this.time = time;
    }

    public String getDay() {
        return day;
    }

    public int getDistance() {
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

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
