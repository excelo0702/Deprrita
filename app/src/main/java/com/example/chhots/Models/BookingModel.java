package com.example.chhots.Models;

public class BookingModel {

    String name,danceForm;
    int imageId;

    public BookingModel(String name, String danceForm, int imageId) {
        this.name = name;
        this.danceForm = danceForm;
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDanceForm() {
        return danceForm;
    }

    public void setDanceForm(String danceForm) {
        this.danceForm = danceForm;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
