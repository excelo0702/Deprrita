package com.example.chhots.Models;

public class HostModel {
    private String info,imageUrl,contestId,date;

    public HostModel() {
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getContestId() {
        return contestId;
    }

    public void setContestId(String contestId) {
        this.contestId = contestId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public HostModel(String info, String imageUrl, String contestId, String date) {
        this.info = info;
        this.imageUrl = imageUrl;
        this.contestId = contestId;
        this.date = date;
    }
}
