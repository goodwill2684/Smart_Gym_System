package com.jicode.smartgymsystem;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

//모든 디비 CRUD작업은 메인스레드가아닌 백그라운드로 작업해야한다. (단, 라이브데이터는 반응시 자기가 알아서 백그라운드로 작업을 처리해준다. )
@Dao
public interface TodoDao {
    @Query("SELECT * FROM Todo")
    LiveData<List<Todo>> getAll(); //LiveData => Todo테이블에 있는 모든 객체를 계속 관찰하고있다가 변경이 일어나면 그것을 자동으로 업데이트하도록한다.
    //getAll() 은 관찰 가능한 객체가 된다.(즉 디비변경시 반응하는)
    @Insert
    void insert(Todo todo);

    @Update
    void update(Todo todo);

    @Delete
    void delete(Todo todo);

    @Query("DELETE FROM Todo")
    void deleteAll();

}