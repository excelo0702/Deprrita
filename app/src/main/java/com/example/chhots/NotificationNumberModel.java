package com.example.chhots;

public class NotificationNumberModel {
    String id;
    int i;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public NotificationNumberModel(String id, int i) {
        this.id = id;
        this.i = i;
    }

    public NotificationNumberModel() {
    }
}
