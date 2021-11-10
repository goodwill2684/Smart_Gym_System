package com.jicode.smartgymsystem;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.jicode.smartgymsystem.Database.Todo;
import com.jicode.smartgymsystem.Database.TodoDao;
import com.jicode.smartgymsystem.Database.TodoDatabase;
import com.jicode.smartgymsystem.VO.DataList;
import com.jicode.smartgymsystem.adapter.DataListAdapter;
import com.jicode.smartgymsystem.adapter.DataListAdapter2;
import com.jicode.smartgymsystem.databinding.ActivityDatabaseBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatabaseActivity extends AppCompatActivity {

    ActivityDatabaseBinding binding;
    TodoDatabase db;
    ArrayList<Todo> dataList;
    ArrayList<Todo> todoList;
    DataListAdapter2 adapter;
    DataListAdapter2 adapter2;

    Date dt;
    Calendar c = Calendar.getInstance();
    int mYear = c.get(Calendar.YEAR);
    int mMonth = c.get(Calendar.MONTH);
    int mDay = c.get(Calendar.DAY_OF_MONTH);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDatabaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //db 생성
        db = TodoDatabase.getAppDatabase(this);

        dataList = new ArrayList<Todo>();
        todoList = new ArrayList<Todo>();
        adapter = new DataListAdapter2(getApplicationContext(), dataList);
        adapter2 = new DataListAdapter2(getApplicationContext(), todoList);
        binding.list.setAdapter(adapter2);

        //UI 갱신 (라이브데이터의 Observer 이용하였음, 해당 디비값이 변화가생기면 실행됨)
        db.todoDao().getAll().observe(this, new Observer<List<Todo>>() {
            @Override
            public void onChanged(List<Todo> todos) {
                todoList.clear();
                todoList.addAll(todos);

            }

        });
        binding.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), adapter2.getItem(i).getTitle().toString(), Toast.LENGTH_SHORT).show();
            }
        });


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DatePickerTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                binding.dateText.setText(String.format("%04d.%02d.%02d", year, month + 1, dayOfMonth));
//
////                lastKey = "0";
//                dataList.clear();
//                if (binding.dateText.getText().length() > 0) {
//                    for (Todo data : todoList) {
//                        String str = binding.dateText.getText().toString();
//                        if (data.getTitle().contains(str)) {
//                            dataList.add(data);
//                        }
//                    }
//                } else dataList.addAll(todoList);
//
//                adapter.notifyDataSetChanged();
//                adapter2.notifyDataSetChanged();

            }
        }, mYear, mMonth, mDay);
        binding.dateText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (binding.dateText.getText().length() > 0) {
//                    for (Todo data : todoList) {
//                        String str = binding.dateText.getText().toString();
//                        if ( data.getTitle().contains(str)) {
//                            dataList.add(data);
//                        }
//                    }
//                }else  dataList.addAll(todoList);
//
//                adapter.notifyDataSetChanged();
//                adapter2.notifyDataSetChanged();

            }

            @Override
            public void afterTextChanged(Editable editable) {

                dataList.clear();

                if (binding.dateText.getText().length() > 0) {
                    for (Todo data : todoList) {
                        String str = binding.dateText.getText().toString();
                        if (data.getTitle().contains(str)) {
                            dataList.add(data);
                        }
                    }
                } if (dataList.size() == 0){
                    Toast.makeText(getApplicationContext(),"현재 선택하신 날에 운동 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                    dataList.addAll(todoList);
                }
                binding.list.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

        });
        binding.calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
    } // Oncreate 끝

    public static class InsertAsyncTask extends AsyncTask<Todo, Void, Void> {
        private TodoDao mTodoDao;

        public InsertAsyncTask(TodoDao todoDao) {
            this.mTodoDao = todoDao;
        }

        @Override
        protected Void doInBackground(Todo... todos) {

            mTodoDao.insert(todos[0]);
            return null;

        }
    }
}
