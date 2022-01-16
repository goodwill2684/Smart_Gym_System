package com.jicode.smartgymsystem;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jicode.smartgymsystem.Lib.JCSharingPreferences;
import com.jicode.smartgymsystem.VO.TrainingLogVO;
import com.jicode.smartgymsystem.databinding.ActivityEventDetailBinding;
import com.jicode.smartgymsystem.databinding.ActivityMyinfomationEditBinding;


public class SetMyInfoActivity extends AppCompatActivity{

    static public SetMyInfoActivity instance;
    JCSharingPreferences preferences;
    ActivityMyinfomationEditBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyinfomationEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        preferences = new JCSharingPreferences(this);
        binding.nameEdit.setText(preferences.getValue("name","Name"));
        binding.emailEdit.setText(preferences.getValue("email","Email"));
        binding.heightEdit.setText(preferences.getValue("height","0"));
        binding.weightEdit.setText(preferences.getValue("weight","0"));
        binding.phoneEdit.setText(preferences.getValue("phone","0"));
        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.nameEdit.length() > 0)
                {
                    if(binding.emailEdit.length() > 0)
                    {
                        if(binding.phoneEdit.length() > 0)
                        {
                            if(binding.heightEdit.length() > 0)
                            {
                                if(binding.weightEdit.length() > 0)
                                {
                                    preferences.putKey("name",binding.nameEdit.getText().toString());
                                    preferences.putKey("email",binding.emailEdit.getText().toString());
                                    preferences.putKey("height",binding.heightEdit.getText().toString());
                                    preferences.putKey("weight",binding.weightEdit.getText().toString());
                                    preferences.putKey("phone",binding.phoneEdit.getText().toString());
                                    onBackPressed();
                                }else Toast.makeText(getApplicationContext(), "무게를 입력해주세요.", Toast.LENGTH_SHORT).show();
                            }else Toast.makeText(getApplicationContext(), "키를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }else Toast.makeText(getApplicationContext(), "휴대폰 번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }else Toast.makeText(getApplicationContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else Toast.makeText(getApplicationContext(), "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    } //OnCreate 끝.





    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.xml.animation1, R.xml.animation2);
    }
}
