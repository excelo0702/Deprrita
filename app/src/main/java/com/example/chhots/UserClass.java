package com.example.chhots;

public class UserClass {
    public UserClass() {
    }

    //videoId====routineId
    private String Id;
    private String date;
    private String category;
    private String cat;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
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

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public UserClass(String id, String date, String category, String cat) {
        Id = id;
        this.date = date;
        this.category = category;
        this.cat = cat;
    }
}
