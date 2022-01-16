package com.jicode.smartgymsystem.VO;

import android.os.Parcel;
import android.os.Parcelable;

public class TrainingLogVO implements Parcelable {
    String time;
    String devicecName;

    float avgCount;
    int totalsetCount;
    float avgWeight;
    int runTime;
    int restTime;

    public TrainingLogVO(String time, String devicecName, float avgCount, int totalsetCount, float avgWeight, int runTime, int restTime) {
        this.time = time;
        this.devicecName = devicecName;
        this.avgCount = avgCount;
        this.totalsetCount = totalsetCount;
        this.avgWeight = avgWeight;
        this.runTime = runTime;
        this.restTime = restTime;
    }

    protected TrainingLogVO(Parcel in) {
        time = in.readString();
        devicecName = in.readString();
        avgCount = in.readFloat();
        totalsetCount = in.readInt();
        avgWeight = in.readFloat();
        runTime = in.readInt();
        restTime = in.readInt();
    }

    public static final Creator<TrainingLogVO> CREATOR = new Creator<TrainingLogVO>() {
        @Override
        public TrainingLogVO createFromParcel(Parcel in) {
            return new TrainingLogVO(in);
        }

        @Override
        public TrainingLogVO[] newArray(int size) {
            return new TrainingLogVO[size];
        }
    };

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDevicecName() {
        return devicecName;
    }

    public void setDevicecName(String devicecName) {
        this.devicecName = devicecName;
    }

    public float getAvgCount() {
        return avgCount;
    }

    public void setAvgCount(float avgCount) {
        this.avgCount = avgCount;
    }

    public int getTotalsetCount() {
        return totalsetCount;
    }

    public void setTotalsetCount(int totalsetCount) {
        this.totalsetCount = totalsetCount;
    }

    public float getAvgWeight() {
        return avgWeight;
    }

    public void setAvgWeight(float avgWeight) {
        this.avgWeight = avgWeight;
    }

    public int getRunTime() {
        return runTime;
    }

    public void setRunTime(int runTime) {
        this.runTime = runTime;
    }

    public int getRestTime() {
        return restTime;
    }

    public void setRestTime(int restTime) {
        this.restTime = restTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(time);
        dest.writeString(devicecName);
        dest.writeFloat(avgCount);
        dest.writeInt(totalsetCount);
        dest.writeFloat(avgWeight);
        dest.writeInt(runTime);
        dest.writeInt(restTime);
    }
}
