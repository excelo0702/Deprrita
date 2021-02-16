package com.example.chhots.ui.Subscription;

public class SubscriptionModel {

    String SubscriptonName,free_trial,plan1,plan2,plan3;
    String Plann1,Plann2,Plann3,Plan4;
    String Id,thumbnail;

    public String getPlan4() {
        return Plan4;
    }

    public void setPlan4(String plan4) {
        Plan4 = plan4;
    }

    public SubscriptionModel(String subscriptonName, String free_trial, String plan1, String plan2, String plan3,String Id) {
        SubscriptonName = subscriptonName;
        this.free_trial = free_trial;
        this.plan1 = plan1;
        this.plan2 = plan2;
        this.plan3 = plan3;
        this.Id = Id;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPlann1() {
        return Plann1;
    }

    public void setPlann1(String plann1) {
        Plann1 = plann1;
    }

    public String getPlann2() {
        return Plann2;
    }

    public void setPlann2(String plann2) {
        Plann2 = plann2;
    }

    public String getPlann3() {
        return Plann3;
    }

    public void setPlann3(String plann3) {
        Plann3 = plann3;
    }

    public SubscriptionModel(String subscriptonName, String free_trial, String plan1, String plan2, String plan3, String plann1, String plann2, String plann3, String plan4, String routineId, String thumbnail) {
        SubscriptonName = subscriptonName;
        this.free_trial = free_trial;
        this.plan1 = plan1;
        this.plan2 = plan2;
        this.plan3 = plan3;
        Plann1 = plann1;
        Plann2 = plann2;
        Plann3 = plann3;
        Plan4 = plan4;
        this.Id = routineId;
        this.thumbnail = thumbnail;
    }

    public String getSubscriptonName() {
        return SubscriptonName;
    }

    public void setSubscriptonName(String subscriptonName) {
        SubscriptonName = subscriptonName;
    }

    public String getFree_trial() {
        return free_trial;
    }

    public void setFree_trial(String free_trial) {
        this.free_trial = free_trial;
    }

    public String getPlan1() {
        return plan1;
    }

    public void setPlan1(String plan1) {
        this.plan1 = plan1;
    }

    public String getPlan2() {
        return plan2;
    }

    public void setPlan2(String plan2) {
        this.plan2 = plan2;
    }

    public String getPlan3() {
        return plan3;
    }

    public void setPlan3(String plan3) {
        this.plan3 = plan3;
    }


    public SubscriptionModel() {
    }
}
