package com.example.chhots.Models;

public class UploadCourseModel {
    private String courseId,userId,courseName,videoUrl;

    public UploadCourseModel() {
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }



    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public UploadCourseModel(String courseId, String userId,String courseName, String videoUrl) {
        this.courseId = courseId;
        this.userId = userId;
        this.courseName = courseName;
        this.videoUrl = videoUrl;
    }
}
