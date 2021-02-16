package com.example.chhots;

public class UserInfoModel {

    private String userId,userEmail,userProfession,userLevel,userImageurl,phone,userName,points,badge;
    private String interest,earn;

    public String getUserId() {
        return userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public UserInfoModel(String userId, String userEmail, String userProfession, String userLevel, String userImageurl, String phone, String userName, String points, String badge, String interest, String earn) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userProfession = userProfession;
        this.userLevel = userLevel;
        this.userImageurl = userImageurl;
        this.phone = phone;
        this.userName = userName;
        this.points = points;
        this.badge = badge;
        this.interest = interest;
        this.earn = earn;
    }

    public UserInfoModel() {
    }


}
