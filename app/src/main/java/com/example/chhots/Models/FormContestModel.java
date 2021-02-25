package com.example.chhots.Models;

public class FormContestModel {
    private String user,contestId,videoUrl;

    public FormContestModel() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContestId() {
        return contestId;
    }

    public void setContestId(String contestId) {
        this.contestId = contestId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public FormContestModel(String user, String contestId, String videoUrl) {
        this.user = user;
        this.contestId = contestId;
        this.videoUrl = videoUrl;
    }
}
