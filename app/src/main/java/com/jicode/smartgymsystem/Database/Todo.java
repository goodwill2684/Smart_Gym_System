package com.jicode.smartgymsystem.Database;

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
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "count")
    private String count;
    @ColumnInfo(name = "value")
    private String value;

    public String getCount() {
        return count;
    }

    public String getValue() {
        return value;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Todo(String title, String count, String value) {
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

    @Override
    public String toString() {
        return "\n id=> " + this.id + " , title=> " + this.title + ", count =>" + this.count + ", value =>" + this.value +"\n";
    }
}
