package com.example.chhots.Models;

public class ExploreVideoModel {

    private String user,thumbnailLink,videoURL,title,videoId;
    private int numberOfLikes,numberOfShare,numberOfViews;
    
    public ExploreVideoModel() {
    }

    public ExploreVideoModel(String user, String thumbnailLink, String videoURL, String title, String videoId, int numberOfLikes, int numberOfShare, int numberOfViews) {
        this.user = user;
        this.thumbnailLink = thumbnailLink;
        this.videoURL = videoURL;
        this.title = title;
        this.videoId = videoId;
        this.numberOfLikes = numberOfLikes;
        this.numberOfShare = numberOfShare;
        this.numberOfViews = numberOfViews;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getThumbnailLink() {
        return thumbnailLink;
    }

    public void setThumbnailLink(String thumbnailLink) {
        this.thumbnailLink = thumbnailLink;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    public void setNumberOfLikes(int numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public int getNumberOfShare() {
        return numberOfShare;
    }

    public void setNumberOfShare(int numberOfShare) {
        this.numberOfShare = numberOfShare;
    }

    public int getNumberOfViews() {
        return numberOfViews;
    }

    public void setNumberOfViews(int numberOfViews) {
        this.numberOfViews = numberOfViews;
    }
}
