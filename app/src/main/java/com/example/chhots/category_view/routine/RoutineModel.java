package com.example.chhots.category_view.routine;

public class RoutineModel {

    private String title,sequenceNo,instructorId,routineId,videoUrl,thumbnail,description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RoutineModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSequenceNo() {
        return sequenceNo;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setSequenceNo(String sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public String getRoutineId() {
        return routineId;
    }

    public void setRoutineId(String routineId) {
        this.routineId = routineId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public RoutineModel(String title, String sequenceNo, String instructorId, String routineId, String videoUrl, String description) {
        this.title = title;
        this.sequenceNo = sequenceNo;
        this.instructorId = instructorId;
        this.routineId = routineId;
        this.videoUrl = videoUrl;
        this.description = description;
    }





}
