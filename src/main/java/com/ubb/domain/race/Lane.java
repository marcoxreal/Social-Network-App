package com.ubb.domain.race;

public class Lane {
    private final int id;
    private final double distance;

    public Lane(int id, double distance) {
        this.id = id;
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public double getDistance() {
        return distance;
    }
}
