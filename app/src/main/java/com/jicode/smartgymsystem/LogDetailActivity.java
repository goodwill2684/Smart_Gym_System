package com.jicode.smartgymsystem;


import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.jicode.smartgymsystem.Adapter.TrainingAdapter;
import com.jicode.smartgymsystem.Adapter.TrainingLogAdapter;
import com.jicode.smartgymsystem.VO.TrainingLogVO;
import com.jicode.smartgymsystem.VO.TrainingVO;
import com.jicode.smartgymsystem.databinding.ActivityEventDetailBinding;

import java.util.ArrayList;


public class LogDetailActivity extends AppCompatActivity{

    ActivityEventDetailBinding binding;
    TrainingLogVO data;
    ArrayList<TrainingVO> datas;
    TrainingAdapter adapter;
    DBHelper dh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dh = new DBHelper(this,"Besporte.db",null,1);
        adapter = new TrainingAdapter();

        data = getIntent().getParcelableExtra("data");
        datas = new ArrayList<>();

        binding.detailTitle.setText(data.getDevicecName());
        binding.dateText.setText(data.getTime().substring(0,4)+"."+data.getTime().substring(4,6)+"."+data.getTime().substring(6,8)+" "+data.getTime().substring(8,10)+":"+data.getTime().substring(10,12)+":"+data.getTime().substring(12));
        binding.avgCount.setText(String.valueOf(data.getAvgCount()));

        binding.setCount.setText(String.valueOf(data.getTotalsetCount()));
        binding.avgWeight.setText(String.valueOf(data.getAvgWeight()));

        int rMin = (int)(data.getRunTime()/600);
        int rSec = (int)((data.getRunTime()%600)/10);
        int reMin = (int)(data.getRestTime()/600);
        int reSec = (int)((data.getRestTime()%600)/10);
        int redSec = (int)((data.getRestTime()%600)%10);

        binding.runTime.setText(String.format("%02d:%02d",rMin,rSec));
        binding.avgRestTime.setText(String.format("%02d:%02d.%d",reMin,reSec,redSec));

        binding.list.setAdapter(adapter);
        binding.list.setLayoutManager(new LinearLayoutManager(this));
        datas.addAll(dh.getDetailData(data.getDevicecName()+"_"+data.getTime()));
        adapter.setmList(datas);

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    } //OnCreate 끝.



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.xml.animation1, R.xml.animation2);
    }
}
