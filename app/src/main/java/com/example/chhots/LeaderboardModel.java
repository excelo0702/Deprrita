package com.example.chhots;


public class LeaderboardModel {
    private String name,points,userId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LeaderboardModel(String name, String points, String userId) {
        this.name = name;
        this.points = points;
        this.userId = userId;
    }

    public LeaderboardModel() {
    }
}