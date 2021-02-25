package com.example.chhots.Models;

public class InstructorInfoModel {

    private String userId,userName,userEmail,userProfession,userPhone,userStatus,userAbout,userLevel,userImageurl,points,badge;
    private String interest,earn;
    private String style;


    public InstructorInfoModel(String userId, String style) {
        this.userId = userId;
        this.style = style;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserAbout() {
        return userAbout;
    }

    public void setUserAbout(String userAbout) {
        this.userAbout = userAbout;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserProfession() {
        return userProfession;
    }

    public void setUserProfession(String userProfession) {
        this.userProfession = userProfession;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }

    public String getUserImageurl() {
        return userImageurl;
    }

    public void setUserImageurl(String userImageurl) {
        this.userImageurl = userImageurl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getEarn() {
        return earn;
    }

    public void setEarn(String earn) {
        this.earn = earn;
    }

    public InstructorInfoModel(String userId, String userName, String userEmail, String userProfession, String userPhone, String userStatus, String userAbout, String userLevel, String userImageurl, String points, String badge, String interest, String earn) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userProfession = userProfession;
        this.userPhone = userPhone;
        this.userStatus = userStatus;
        this.userAbout = userAbout;
        this.userLevel = userLevel;
        this.userImageurl = userImageurl;
        this.points = points;
        this.badge = badge;
        this.interest = interest;
        this.earn = earn;
    }

    public InstructorInfoModel() {
    }


}
