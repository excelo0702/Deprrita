package com.example.chhots.category_view.routine;

public class PreviewModel {
    String id,url,userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public PreviewModel(String id, String url, String userId) {
        this.id = id;
        this.url = url;
        this.userId = userId;
    }

    public PreviewModel() {
    }
}
