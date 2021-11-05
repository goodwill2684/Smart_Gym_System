package com.jicode.smartgymsystem;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;

import com.jicode.smartgymsystem.Database.Todo;
import com.jicode.smartgymsystem.Database.TodoDao;
import com.jicode.smartgymsystem.Database.TodoDatabase;
import com.jicode.smartgymsystem.databinding.ActivityDatabaseBinding;

import java.util.List;

public class DatabaseActivity extends AppCompatActivity {

    ActivityDatabaseBinding binding;
    TodoDatabase db;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDatabaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //db 생성
        db = TodoDatabase.getAppDatabase(this);

        //UI 갱신 (라이브데이터의 Observer 이용하였음, 해당 디비값이 변화가생기면 실행됨)
        db.todoDao().getAll().observe(this, new Observer<List<Todo>>() {
            @Override
            public void onChanged(List<Todo> todos) {
                binding.dataView.setText(todos.toString());
            }
        });

        binding.dataView.setText(db.todoDao().getAll().toString());
        binding.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override 
            public void onClick(View view) {
                if(binding.etSetData.getText().toString().trim().length() <= 0) {
                    Toast.makeText(getApplicationContext(), "한글자 이상입력해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    String count = "";
                    String value = "";
                    new InsertAsyncTask(db.todoDao()).execute(new Todo(binding.etSetData.getText().toString(),count,value));
                    binding.etSetData.setText("");

                }
            }

        });

    } // Oncreate 끝

    public static class InsertAsyncTask extends AsyncTask<Todo, Void, Void> {
        private TodoDao mTodoDao;

        public  InsertAsyncTask(TodoDao todoDao){
            this.mTodoDao = todoDao;
        }
        @Override
        protected Void doInBackground(Todo... todos) {

            mTodoDao.insert(todos[0]);
            return null;

        }
    }
}
