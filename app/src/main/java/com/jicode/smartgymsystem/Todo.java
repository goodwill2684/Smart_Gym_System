package com.jicode.smartgymsystem;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//@Entity(primaryKeys = {"firstName", "lastName"}) =>
//@Entity(tableName = "Todo")

@Entity
public class Todo {
    @PrimaryKey(autoGenerate = true) //autoGenerate는 알아서 id를 1씩 증가 autoincrement와 동일.
    private int id;
    //@ColumnInfo(name = "first_name") ==>컬럼명 변수명과 다르게 사용 가능
    private String title;

    public Todo(String title) {
        this.title = title;
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

    @Override
    public String toString() {
        return "\n id=> " + this.id + " , title=> " + this.title;
    }
}
