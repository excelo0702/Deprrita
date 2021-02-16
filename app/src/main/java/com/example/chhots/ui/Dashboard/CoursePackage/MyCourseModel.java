package com.example.chhots.ui.Dashboard.CoursePackage;

public class MyCourseModel {
    String courseName,courseId,courseThumbnail,money;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseThumbnail() {
        return courseThumbnail;
    }

    public void setCourseThumbnail(String courseThumbnail) {
        this.courseThumbnail = courseThumbnail;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public MyCourseModel() {
    }

    public MyCourseModel(String courseName, String courseId, String courseThumbnail, String money) {
        this.courseName = courseName;
        this.courseId = courseId;
        this.courseThumbnail = courseThumbnail;
        this.money = money;
    }
}
