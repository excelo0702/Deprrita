package com.example.chhots.Models;

public class UserModel {

    private String name;
    private String email;
    private String about;
    private String phone;
    private String image;

    public UserModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public UserModel(String name, String email, String about, String phone, String image) {
        this.name = name;
        this.email = email;
        this.about = about;
        this.phone = phone;
        this.image = image;
    }
}
