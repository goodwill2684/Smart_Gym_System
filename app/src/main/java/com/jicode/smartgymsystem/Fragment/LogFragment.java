package com.jicode.smartgymsystem.Fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jicode.smartgymsystem.Adapter.TrainingLogAdapter;
import com.jicode.smartgymsystem.DBHelper;
import com.jicode.smartgymsystem.R;
import com.jicode.smartgymsystem.VO.TrainingLogVO;
import com.jicode.smartgymsystem.databinding.FragmentLogBinding;

import java.util.ArrayList;
import java.util.Calendar;

public class LogFragment extends Fragment {
    DBHelper dh;
    FragmentLogBinding binding;
    TrainingLogAdapter adapter;

    String lastKey = "0";

    ArrayList<TrainingLogVO> dataList;

    Calendar c = Calendar.getInstance();
    int mYear = c.get(Calendar.YEAR);
    int mMonth = c.get(Calendar.MONTH);
    int mDay = c.get(Calendar.DAY_OF_MONTH);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentLogBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        dh = new DBHelper(getContext(),"Besporte.db",null,1);
        dataList = new ArrayList<TrainingLogVO>();
        adapter = new TrainingLogAdapter(getActivity());

        binding.list.setAdapter(adapter);
        binding.list.setLayoutManager(new LinearLayoutManager(getContext()));
        dataList.addAll(dh.getTableDatas(null));
        adapter.setmList(dataList);
        lastKey = "0";

        adapter.notifyDataSetChanged();

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                binding.dateText.setText(year+"."+(month+1)+"."+dayOfMonth);
                mYear = year;
                mMonth = month;
                mDay = dayOfMonth;
                mDay = dayOfMonth;
                dataList.clear();
                dataList.addAll(dh.getTableDatas(String.format("%04d",year)+String.format("%02d",month+1)+String.format("%02d",dayOfMonth)));
                adapter.notifyDataSetChanged();
            }
        }, mYear, mMonth, mDay);

        binding.eventTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.dateText.setText("All");
                dataList.clear();
                dataList.addAll(dh.getTableDatas(null));
                adapter.notifyDataSetChanged();
            }
        });
        binding.calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
        lastKey = "0";
        return root;
    }

    public void LogUpdate()
    {
        binding.dateText.setText("All");
        dataList.clear();
        dataList.addAll(dh.getTableDatas(null));
        adapter.notifyDataSetChanged();
    }

}
