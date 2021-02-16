package com.example.chhots.ui.Subscription;

import org.reactivestreams.Subscription;

public class SUbscriptionViewModel {
    public SUbscriptionViewModel() {
    }

    String category;
    double views;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getViews() {
        return views;
    }

    public void setViews(double views) {
        this.views = views;
    }

    public SUbscriptionViewModel(String category, double views) {
        this.category = category;
        this.views = views;
    }
}
