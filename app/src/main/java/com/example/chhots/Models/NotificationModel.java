package com.example.chhots.Models;

public class NotificationModel {
    private String date,category,userName;
    private String description;
    private String thumbnail;

    public NotificationModel() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public NotificationModel(String date, String category, String userName, String description, String thumbnail) {
        this.date = date;
        this.category = category;
        this.userName = userName;
        this.description = description;
        this.thumbnail = thumbnail;
    }
}
