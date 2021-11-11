package com.jicode.smartgymsystem.VO;

import android.os.Parcel;
import android.os.Parcelable;

public class TrainingVO  {
    String deviceName;
    String time;
    float weight;
    int count;
    String runningTime;
    String restTime;
    int setcount;

    public TrainingVO(String deviceName, String time, float weight, int count, String runningTime, String restTime, int setcount) {
        this.deviceName = deviceName;
        this.time = time;
        this.weight = weight;
        this.count = count;
        this.runningTime = runningTime;
        this.restTime = restTime;
        this.setcount = setcount;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getRunningTime() {
        return runningTime;
    }

    public void setRunningTime(String runningTime) {
        this.runningTime = runningTime;
    }

    public String getRestTime() {
        return restTime;
    }

    public void setRestTime(String restTime) {
        this.restTime = restTime;
    }

    public int getSetcount() {
        return setcount;
    }

    public void setSetcount(int setcount) {
        this.setcount = setcount;
    }
}
