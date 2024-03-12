package com.example.fitnesstrack.adapters;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskNameView implements Parcelable {
    private String name;

    public TaskNameView(Parcel in) {
        name = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskNameView(String name) {
        this.name = name;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TaskNameView> CREATOR = new Creator<TaskNameView>() {
        @Override
        public TaskNameView createFromParcel(Parcel in) {
            return new TaskNameView(in);
        }

        @Override
        public TaskNameView[] newArray(int size) {
            return new TaskNameView[size];
        }
    };
}
