package com.example.chhots.ui.Dashboard;


public class PointModel {
    String id;
    int points;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public PointModel(String id, int points) {
        this.id = id;
        this.points = points;
    }

    public PointModel() {
    }
}
