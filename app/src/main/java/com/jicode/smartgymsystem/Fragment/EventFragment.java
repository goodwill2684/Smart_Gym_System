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
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jicode.smartgymsystem.Adapter.EventAdapter;
import com.jicode.smartgymsystem.R;
import com.jicode.smartgymsystem.VO.EventVO;
import com.jicode.smartgymsystem.databinding.FragmentEventBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class EventFragment extends Fragment {

    FragmentEventBinding binding;
    EventAdapter adapter;

    String lastKey = "0";

    ArrayList<EventVO> dataList;

    Date dt;

    Calendar c = Calendar.getInstance();
    int mYear = c.get(Calendar.YEAR);
    int mMonth = c.get(Calendar.MONTH);
    int mDay = c.get(Calendar.DAY_OF_MONTH);

    boolean dataLoadCheck = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_event, container, false);
        View root = binding.getRoot();

        dataList = new ArrayList<EventVO>();
        adapter = new EventAdapter();

        binding.list.setAdapter(adapter);
        binding.list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setmList(dataList);

        lastKey = "0";
        dataList.add(new EventVO("20211103123421","Pull-Down",40L,10L,"7:11",5L));
        dataList.add(new EventVO("20211103124411","Bench-Press",55L,10L,"4:07",5L));
        dataList.add(new EventVO("20211103125032","Shoulder-Press",50L,12L,"6:34",5L));
        dataList.add(new EventVO("20211103130126","Squat",70L,6L,"5:11",5L));
        dataList.add(new EventVO("20211103131055","Arm-Curl",50L,10L,"6:00",5L));

        adapter.notifyDataSetChanged();

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                binding.dateText.setText(year+"."+(month+1)+"."+dayOfMonth);
                mYear = year;
                mMonth = month;
                mDay = dayOfMonth;
                lastKey = "0";
                dataList.clear();
                if(!dataLoadCheck) Toast.makeText(getContext(), "load", Toast.LENGTH_SHORT).show();;
            }
        }, mYear, mMonth, mDay);

        binding.calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
        lastKey = "0";
        if(!dataLoadCheck) Toast.makeText(getContext(), "load", Toast.LENGTH_SHORT).show();;
        return root;
    }

    public void loadSQLite()
    {

        dataLoadCheck = false;
    }
}
