package com.example.chhots.ui.Dashboard.ApproveVideo;

public class MyRoutineModel {
    private String routineName,routineId,routineThumbnail,money;

    public String getRoutineName() {
        return routineName;
    }

    public void setRoutineName(String routineName) {
        this.routineName = routineName;
    }

    public String getRoutineId() {
        return routineId;
    }

    public void setRoutineId(String routineId) {
        this.routineId = routineId;
    }

    public String getRoutineThumbnail() {
        return routineThumbnail;
    }

    public void setRoutineThumbnail(String routineThumbnail) {
        this.routineThumbnail = routineThumbnail;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public MyRoutineModel(String routineName, String routineId, String routineThumbnail, String money) {
        this.routineName = routineName;
        this.routineId = routineId;
        this.routineThumbnail = routineThumbnail;
        this.money = money;
    }

    public MyRoutineModel() {
    }
}
