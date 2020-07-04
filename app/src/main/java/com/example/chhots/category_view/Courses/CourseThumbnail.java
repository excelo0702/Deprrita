package com.example.chhots.category_view.Courses;

public class CourseThumbnail {

    private String CourseName,CourseId,CourseImage;
    private String instructorId;
    private int views;
    private double rating,trending;
    private String date;
    private String category,learn,description,time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public CourseThumbnail(String courseName, String courseId, String courseImage, String instructorId, int views, double rating, double trending, String date, String category, String learn, String description, String time) {
        CourseName = courseName;
        CourseId = courseId;
        CourseImage = courseImage;
        this.instructorId = instructorId;
        this.views = views;
        this.rating = rating;
        this.trending = trending;
        this.date = date;
        this.category = category;
        this.learn = learn;
        this.description = description;
        this.time = time;
    }

    public String getLearn() {
        return learn;
    }

    public void setLearn(String learn) {
        this.learn = learn;
    }

    public CourseThumbnail(String courseName, String courseId, String courseImage, String instructorId, int views, double rating, double trending, String date, String category, String learn, String description) {
        CourseName = courseName;
        CourseId = courseId;
        CourseImage = courseImage;
        this.instructorId = instructorId;
        this.views = views;
        this.rating = rating;
        this.trending = trending;
        this.date = date;
        this.category = category;
        this.learn = learn;
        this.description = description;
    }

    public String getCourseName() {
        return CourseName;
    }

    public void setCourseName(String courseName) {
        CourseName = courseName;
    }

    public String getCourseId() {
        return CourseId;
    }

    public void setCourseId(String courseId) {
        CourseId = courseId;
    }

    public String getCourseImage() {
        return CourseImage;
    }

    public void setCourseImage(String courseImage) {
        CourseImage = courseImage;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTrending() {
        return trending;
    }

    public void setTrending(double trending) {
        this.trending = trending;
    }

    public CourseThumbnail() {
    }
}
