package com.jicode.smartgymsystem.VO;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.LinearLayoutCompat;

public class TrainingVO  {
    float weight;
    int count;
    int runningTime;
    int restTime;
    int setcount;
    boolean setlayout = false;

    public TrainingVO(float weight, int count, int runningTime, int restTime, int setcount) {
        this.weight = weight;
        this.count = count;
        this.runningTime = runningTime;
        this.restTime = restTime;
        this.setcount = setcount;
    }

    public boolean isSetlayout() {
        return setlayout;
    }

    public void setSetlayout(boolean setlayout) {
        this.setlayout = setlayout;
    }


    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getRunningTime() {
        return runningTime;
    }

    public void setRunningTime(int runningTime) {
        this.runningTime = runningTime;
    }

    public int getRestTime() {
        return restTime;
    }

    public void setRestTime(int restTime) {
        this.restTime = restTime;
    }

    public int getSetcount() {
        return setcount;
    }

    public void setSetcount(int setcount) {
        this.setcount = setcount;
    }
}
