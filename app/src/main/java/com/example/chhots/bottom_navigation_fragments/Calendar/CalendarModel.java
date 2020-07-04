package com.example.chhots.bottom_navigation_fragments.Calendar;

public class CalendarModel {


    public CalendarModel() {
    }

    private String category,date,title,description,time,url;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public CalendarModel(String category, String date, String title, String description, String time, String url) {
        this.category = category;
        this.date = date;
        this.title = title;
        this.description = description;
        this.time = time;
        this.url = url;
    }
}
