package com.jicode.smartgymsystem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;
import com.jicode.smartgymsystem.Fragment.LogFragment;
import com.jicode.smartgymsystem.VO.TrainingLogVO;
import com.jicode.smartgymsystem.VO.TrainingVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DBHelper extends SQLiteOpenHelper {

    private Context context;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {   // 생성자에서 얻어온 데이터 베이스가 존재하지 않을 경우 최초 1회 실행
        sqLiteDatabase.execSQL("CREATE TABLE DATA_TABLE (DeviceName TEXT,Date TEXT, Time TEXT, AvgCount REAL,TotlaSetCount INTEGER,  AvgWeight REAL, RunTime INTEGER, AvgRestTime INTEGER);"); // 테이블 생성 date, time, data, check 값을 칼럼으로 가지는 테이블
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Toast.makeText(context, "버전이 올라갔습니다.", Toast.LENGTH_SHORT).show();
    }

    public void insert(String deviceName, ArrayList<TrainingVO> trainingVOS)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat currentTime = new SimpleDateFormat("yyyyMMddHHmmss");
        String current = currentTime.format(new Date());

        String date = current.substring(0,8);
        String time = current.substring(8);

        float avgCount = 0;
        float avgWeight = 0;
        int restTime =0;
        db.execSQL("CREATE TABLE "+deviceName+"_"+current+" (Count INTEGER,SetCount INTEGER,Weight REAL,RunTime INTEGER,RestTime INTEGER);");
        for(TrainingVO data : trainingVOS)
        {
            avgWeight+=data.getWeight();
            restTime+=data.getRestTime();
            String sql = "INSERT INTO "+deviceName+"_"+current+ " VALUES("+String.format(data.getCount()+","+data.getSetcount()+","+data.getWeight()+","+data.getRunningTime()+","+data.getRestTime()+");");
            db.execSQL(sql);
        }
        avgWeight = avgWeight/trainingVOS.size();
        if(trainingVOS.size() > 1) restTime = restTime / trainingVOS.size() - 1;
        avgCount = trainingVOS.size() / trainingVOS.get(trainingVOS.size()-1).getSetcount();


        String sql = String.format("INSERT INTO DATA_TABLE VALUES('"+deviceName+"','"+date+"','"+time+"',"+avgCount+","+trainingVOS.get(trainingVOS.size()-1).getSetcount()+","+avgWeight+","+trainingVOS.get(trainingVOS.size()-1).getRunningTime()+","+restTime+");");
        db.execSQL(sql);
        MainActivity.instance.logUpdate();
    }

    public ArrayList<TrainingLogVO> getTableDatas(String date)
    {
        ArrayList<TrainingLogVO> datas = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String sql = String.format("SELECT * FROM DATA_TABLE");
        if(date != null) sql = String.format("SELECT * FROM DATA_TABLE WHERE date = '"+date+"'");    // 해당 날짜의 데이터 검색
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0)                                                                              //데이터베이스 검색 결과가 존재할 시
            while (cursor.moveToNext()) {                                                                       // 데이터들을 데이터 리스트에 담음
                datas.add(new TrainingLogVO(cursor.getString(1)+cursor.getString(2),cursor.getString(0),
                        cursor.getFloat(3),cursor.getInt(4),cursor.getFloat(5),cursor.getInt(6),cursor.getInt(7)));                                                                   // 데이터 리스트에 저장
            }
        return datas;
    }

    public ArrayList<TrainingVO> getDetailData(String tableName)
    {
        ArrayList<TrainingVO> datas = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String sql = String.format("SELECT * FROM "+tableName);
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0)                                                                              //데이터베이스 검색 결과가 존재할 시
            while (cursor.moveToNext()) {                                                                       // 데이터들을 데이터 리스트에 담음
                datas.add(new TrainingVO(cursor.getFloat(2),cursor.getInt(0),cursor.getInt(3),cursor.getInt(4),cursor.getInt(1)));                                                                   // 데이터 리스트에 저장
            }
        return datas;
    }
}
