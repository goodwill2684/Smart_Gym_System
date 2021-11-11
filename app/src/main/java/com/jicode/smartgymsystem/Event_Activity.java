package com.jicode.smartgymsystem;


import android.Manifest;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;


import com.jicode.smartgymsystem.VO.EventVO;
import com.jicode.smartgymsystem.databinding.ActivityEventDetailBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class Event_Activity extends AppCompatActivity{

    static public Event_Activity instance;


    private static final String TAG = "googlemap_ORG";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    boolean needRequest = false;

    double lati = 0;
    double longi = 0;

    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소


    Handler handler;
    boolean playFlag = false;

    ActivityEventDetailBinding binding;
    EventVO data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_event_detail);
        
        data = getIntent().getParcelableExtra("data");

        binding.detailTitle.setText(data.getEvent());
        binding.dateText.setText(data.getTime().substring(0,4)+"."+data.getTime().substring(4,6)+"."+data.getTime().substring(6,8)+" "+data.getTime().substring(8,10)+":"+data.getTime().substring(10,12)+":"+data.getTime().substring(12));
        binding.eventName.setText(data.getEvent());


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
    }
}
