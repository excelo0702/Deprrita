package com.example.chhots.category_view.Courses;

public class CourseVideoModel {
    private String title,seq_no,videoUrl,description,courseId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSeq_no() {
        return seq_no;
    }

    public void setSeq_no(String seq_no) {
        this.seq_no = seq_no;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public CourseVideoModel(String title, String seq_no, String videoUrl, String description, String courseId) {
        this.title = title;
        this.seq_no = seq_no;
        this.videoUrl = videoUrl;
        this.description = description;
        this.courseId = courseId;
    }
}
