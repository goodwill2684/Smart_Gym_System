package com.jicode.smartgymsystem;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DBHelper extends SQLiteOpenHelper {

    private Context context;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {   // 생성자에서 얻어온 데이터 베이스가 존재하지 않을 경우 최초 1회 실행

        sqLiteDatabase.execSQL("CREATE TABLE DATA_TABLE (date TEXT, time TEXT, data INTEGER, checkf INTEGER);"); // 테이블 생성 date, time, data, check 값을 칼럼으로 가지는 테이블
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Toast.makeText(context, "버전이 올라갔습니다.", Toast.LENGTH_SHORT).show();
    }

    public void insert(String data, int check)      //데이터와 check값을 넣으면 현채 시간과 날짜를 함께 넣은 엔티티 생성
    {
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat currentTime = new SimpleDateFormat("yyyyMMddHHmmss");
        String current = currentTime.format(new Date());

        String date = current.substring(0,8);
        String time = current.substring(8);

        String sql = String.format("INSERT INTO DATA_TABLE VALUES('"+date+"','"+time+"',"+data+","+check+");");
        db.execSQL(sql);
    }

}
