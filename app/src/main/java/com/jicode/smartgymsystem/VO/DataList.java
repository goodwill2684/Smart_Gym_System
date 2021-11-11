package com.jicode.smartgymsystem.VO;

import androidx.room.ColumnInfo;

public class DataList {
    public int id;
    private String title;
    private String count;
    private String value;

    public DataList(int id, String title, String count, String value) {
        this.id = id;
        this.title = title;
        this.count = count;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
