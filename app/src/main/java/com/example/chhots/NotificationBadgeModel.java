package com.example.chhots;

import java.util.HashMap;
import java.util.Map;

public class NotificationBadgeModel {
    public NotificationBadgeModel() {
    }

    int notification_dahboard=0,notification_approve_videos=0,notification_routines=0;
    int notification_notification=0;
    HashMap<String,Integer> notification_chatlist = new HashMap<>();

    public HashMap<String, Integer> getNotification_chatlist() {
        return notification_chatlist;
    }

    public void setNotification_chatlist(HashMap<String, Integer> notification_chatlist) {
        this.notification_chatlist = notification_chatlist;
    }

    public int getNotification_dahboard() {
        return notification_dahboard;
    }

    public void setNotification_dahboard(int notification_dahboard) {
        this.notification_dahboard = notification_dahboard;
    }

    public int getNotification_approve_videos() {
        return notification_approve_videos;
    }

    public void setNotification_approve_videos(int notification_approve_videos) {
        this.notification_approve_videos = notification_approve_videos;
    }

    public int getNotification_routines() {
        return notification_routines;
    }

    public void setNotification_routines(int notification_routines) {
        this.notification_routines = notification_routines;
    }


    public int getNotification_notification() {
        return notification_notification;
    }

    public void setNotification_notification(int notification_notification) {
        this.notification_notification = notification_notification;
    }
}
