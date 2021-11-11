package com.jicode.smartgymsystem.VO;

import android.os.Parcel;
import android.os.Parcelable;

public class EventVO implements Parcelable {
    String time;
    String event;
    Long wight;
    Long count;
    String RunningTime;
    long setcount;

    protected EventVO(Parcel in) {
        time = in.readString();
        event = in.readString();
        if (in.readByte() == 0) {
            wight = null;
        } else {
            wight = in.readLong();
        }
        if (in.readByte() == 0) {
            count = null;
        } else {
            count = in.readLong();
        }
        RunningTime = in.readString();
        setcount = in.readLong();
    }

    public static final Creator<EventVO> CREATOR = new Creator<EventVO>() {
        @Override
        public EventVO createFromParcel(Parcel in) {
            return new EventVO(in);
        }

        @Override
        public EventVO[] newArray(int size) {
            return new EventVO[size];
        }
    };

    public long getSetcount() {
        return setcount;
    }

    public void setSetcount(long setcount) {
        this.setcount = setcount;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public void setWight(Long wight) {
        this.wight = wight;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public void setRunningTime(String runningTime) {
        RunningTime = runningTime;
    }

    public EventVO(String time, String event, Long wight, Long count, String runningTime, long setcount) {
        this.time = time;
        this.event = event;
        this.wight = wight;
        this.count = count;
        RunningTime = runningTime;
        this.setcount = setcount;
    }

    public String getTime() {
        return time;
    }

    public String getEvent() {
        return event;
    }

    public Long getWight() {
        return wight;
    }

    public Long getCount() {
        return count;
    }

    public String getRunningTime() {
        return RunningTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(time);
        dest.writeString(event);
        if (wight == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(wight);
        }
        if (count == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(count);
        }
        dest.writeString(RunningTime);
        dest.writeLong(setcount);
    }
}
