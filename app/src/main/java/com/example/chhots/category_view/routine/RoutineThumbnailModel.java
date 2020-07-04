package com.example.chhots.category_view.routine;

import android.os.Parcel;
import android.os.Parcelable;

public class RoutineThumbnailModel implements Parcelable {
    String title,instructor_name,routine_views,routine_level,routineId,routineThumbnail,instructorId,routineDescription,learn;

    String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public RoutineThumbnailModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInstructor_name() {
        return instructor_name;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public void setInstructor_name(String instructor_name) {
        this.instructor_name = instructor_name;
    }

    public String getRoutine_views() {
        return routine_views;
    }

    public void setRoutine_views(String routine_views) {
        this.routine_views = routine_views;
    }

    public String getRoutine_level() {
        return routine_level;
    }

    public void setRoutine_level(String routine_level) {
        this.routine_level = routine_level;
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

    public String getRoutineDescription() {
        return routineDescription;
    }

    public void setRoutineDescription(String routineDescription) {
        this.routineDescription = routineDescription;
    }

    public String getLearn() {
        return learn;
    }

    public void setLearn(String learn) {
        this.learn = learn;
    }

    public RoutineThumbnailModel(String title, String instructor_name, String routine_views, String routine_level, String routineId, String routineThumbnail, String instructorId, String routineDescription, String learn, String category) {
        this.title = title;
        this.instructor_name = instructor_name;
        this.routine_views = routine_views;
        this.routine_level = routine_level;
        this.routineId = routineId;
        this.routineThumbnail = routineThumbnail;
        this.instructorId = instructorId;
        this.routineDescription = routineDescription;
        this.learn = learn;
        this.category = category;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(instructor_name);
        parcel.writeString(routine_views);
        parcel.writeString(routine_level);
        parcel.writeString(routineId);
        parcel.writeString(routineThumbnail);
        parcel.writeString(instructorId);
        parcel.writeString(routineDescription);
        parcel.writeString(category);
    }

    public RoutineThumbnailModel(Parcel in)
    {
        title = in.readString();
        instructor_name = in.readString();
        routine_views = in.readString();
        routine_level = in.readString();
        routineId = in.readString();
        routineThumbnail = in.readString();
        instructorId = in.readString();
        routineDescription = in.readString();
        category = in.readString();

    }

    public static final Parcelable.Creator<RoutineThumbnailModel> CREATOR = new Parcelable.Creator<RoutineThumbnailModel>()
    {
        public RoutineThumbnailModel createFromParcel(Parcel in)
        {
            return new RoutineThumbnailModel(in);
        }
        public RoutineThumbnailModel[] newArray(int size)
        {
            return new RoutineThumbnailModel[size];
        }
    };


}
