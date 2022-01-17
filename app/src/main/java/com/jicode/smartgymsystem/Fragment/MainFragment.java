package com.jicode.smartgymsystem.Fragment;


import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.clj.fastble.BleManager;
import com.jicode.smartgymsystem.DBHelper;
import com.jicode.smartgymsystem.Lib.JCSharingPreferences;
import com.jicode.smartgymsystem.MainActivity;
import com.jicode.smartgymsystem.R;
import com.jicode.smartgymsystem.VO.TrainingVO;

import java.util.Timer;
import java.util.TimerTask;

import java.util.ArrayList;


public class MainFragment extends Fragment {
    DBHelper dh;
    static public MainFragment instance;
    public TextView countText,nameText,weightText,runTime,restTime,setText;
    public Button setBtn, stopBtn, setWeight, disconnect;
    public ImageView img_loading;
    public SoundPool soundPool;
    public ArrayList<Integer> countSound;
    public ArrayList<TrainingVO> trainingVOS;

    TimerTask timerTask;
    Timer timer = new Timer();
    public int timeCount = 0;
    public int preTimeCount = 0;
    public int count = 0;
    public int setCount = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_sports, container, false);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        instance = this;
        dh = new DBHelper(getContext(),"Besporte.db",null,1);
        countSound = new ArrayList<>();
        soundPool = new SoundPool(15, AudioManager.STREAM_MUSIC,0);
        trainingVOS = new ArrayList<>();
        setSoundPool();
        countText = root.findViewById(R.id.count);
        nameText = root.findViewById(R.id.name);

        weightText = root.findViewById(R.id.weight);
        runTime = root.findViewById(R.id.runtimetext);
        restTime = root.findViewById(R.id.resttime);
        setText = root.findViewById(R.id.settext);

        img_loading = root.findViewById(R.id.img_loading);
        setBtn = root.findViewById(R.id.setbtn);
        stopBtn = root.findViewById(R.id.stopbtn);
        setWeight = root.findViewById(R.id.setweight);
        disconnect = root.findViewById(R.id.disconnect);

        disconnect.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              try {
                                                  BleManager.getInstance().disconnectAllDevice();
                                                  disconnect.setVisibility(View.INVISIBLE);
                                              }catch(Exception e)
                                              {}
                                          }
                                      }
        );
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count != 0) {
                    count = 0;
                    countText.setText(String.valueOf(count));
                    setText.setText(String.valueOf(++setCount));
                }
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initText();
            }
        });
        setWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.instance.sendData("$wr;");
            }
        });
            return root;
    } //OnCreate 끝.

    void setSoundPool()
    {
        countSound.add(soundPool.load(getContext(),R.raw.s1,0));
        countSound.add(soundPool.load(getContext(),R.raw.s2,0));
        countSound.add(soundPool.load(getContext(),R.raw.s3,0));
        countSound.add(soundPool.load(getContext(),R.raw.s4,0));
        countSound.add(soundPool.load(getContext(),R.raw.s5,0));
        countSound.add(soundPool.load(getContext(),R.raw.s6,0));
        countSound.add(soundPool.load(getContext(),R.raw.s7,0));
        countSound.add(soundPool.load(getContext(),R.raw.s8,0));
        countSound.add(soundPool.load(getContext(),R.raw.s9,0));
        countSound.add(soundPool.load(getContext(),R.raw.s10,0));
        countSound.add(soundPool.load(getContext(),R.raw.s11,0));
        countSound.add(soundPool.load(getContext(),R.raw.s12,0));
        countSound.add(soundPool.load(getContext(),R.raw.s13,0));
        countSound.add(soundPool.load(getContext(),R.raw.s14,0));
        countSound.add(soundPool.load(getContext(),R.raw.s15,0));
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    private void startTimerTask() // 카운트 시작
    {
        timeCount = 0;
        preTimeCount = 0;
        runTime.setText("00:00");
        restTime.setText("00:00.0");
        setText.setText(String.valueOf(++setCount));
        trainingVOS.clear();
        stopTimerTask();
        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timeCount++;
                        preTimeCount++;

                        int rMin = (int)(timeCount/600);
                        int rSec = (int)((timeCount%600)/10);
                        int reMin = (int)(preTimeCount/600);
                        int reSec = (int)((preTimeCount%600)/10);
                        int redSec = (int)((preTimeCount%600)%10);

                        runTime.setText(String.format("%02d:%02d",rMin,rSec));
                        restTime.setText(String.format("%02d:%02d.%d",reMin,reSec,redSec));
                    }
                });

            }
        };
        timer.schedule(timerTask,0 ,100);
    }

    public void plusCount(int weight)
    {
        weightText.setText(String.valueOf(weight)+"KG");
        if(count == 0 && setCount == 0)
            startTimerTask();
        if(count < 15)
            soundPool.play(countSound.get(count),1f,1f,0,0,1f);

        countText.setText(String.valueOf(++count)+"개");

        trainingVOS.add(new TrainingVO(weight,count,timeCount,preTimeCount,setCount));
        preTimeCount = 0;
        restTime.setText("00:00.0");
    }

    private void stopTimerTask()
    {
        if(timerTask != null)
        {
            dh.insert(nameText.getText().toString(),trainingVOS);
            timeCount = 0;
            preTimeCount = 0;
            runTime.setText("00:00");
            restTime.setText("00:00.0");
            trainingVOS.clear();
            timerTask.cancel();
            timerTask = null;
        }
    }

    public void initText()
    {
        stopTimerTask();
        count = 0;
        timeCount = 0;
        preTimeCount = 0;
        setText.setText("0");
        weightText.setText("0KG");
        runTime.setText("00:00");
        restTime.setText("00:00.0");
        countText.setText("0개");
    }


}
