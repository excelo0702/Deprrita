package com.example.chhots.LetsGetStarted;

public class LetsGetStartedModel {
    private String welcome,description;
    private int startimage;

    public String getWelcome() {
        return welcome;
    }

    public void setWelcome(String welcome) {
        this.welcome = welcome;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStartimage() {
        return startimage;
    }

    public void setStartimage(int startimage) {
        this.startimage = startimage;
    }

    public LetsGetStartedModel(String welcome, String description, int startimage) {
        this.welcome = welcome;
        this.description = description;
        this.startimage = startimage;
    }

    public LetsGetStartedModel() {
    }
}
