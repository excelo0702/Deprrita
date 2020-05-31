package com.example.chhots.ui.Category;

public class CategoryModel {
    String text;
    int image;
    int l=0;
    int u=18;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public CategoryModel() {
    }

    public int getL() {
        return l;
    }

    public void setL(int l) {
        this.l = l;
    }

    public int getU() {
        return u;
    }

    public void setU(int u) {
        this.u = u;
    }

    public CategoryModel(String text, int image, int l, int u) {
        this.text = text;
        this.image = image;
        this.l = l;
        this.u = u;
    }
}
