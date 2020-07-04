package com.example.chhots.ui.Dashboard;

public class PointModel {
    private String id,name;
    private int points;


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



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PointModel(String id, String name, int points) {
        this.id = id;
        this.name = name;
        this.points = points;
    }

    public PointModel() {
    }
}
