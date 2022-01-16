package com.jicode.smartgymsystem.Popup;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jicode.smartgymsystem.MainActivity;
import com.jicode.smartgymsystem.R;


public class Callibration_PinLoad_Popup extends Activity {
    Button ok, cancel;
    TextView levelText,contentText;
    int index = 0;

    public static Callibration_PinLoad_Popup instance = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_pin_callibration);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        instance = this;
        ok=(Button)findViewById(R.id.ok);
        levelText = findViewById(R.id.level);
        contentText = findViewById(R.id.contenttext);
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.instance.sendData("$re;");
                finish();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClose(view);
            }
        });

        MainActivity.instance.sendData("$rd;");
    }

    public void setDist(String dist)
    {
        if(index == 0) contentText.setText("인식테스트\n"+dist+"cm");
    }
    public void mOnClose(View v){

        switch(++index)
        {
            case 1:
                ok.setText("다음");
                levelText.setText("2/5");
                contentText.setText("무게핀을 첫 번째 판에 \n연결한 후 다음버튼을 \n눌러주세요.");
                MainActivity.instance.sendData("$de;");
                break;
            case 2:
                ok.setText("다음");
                levelText.setText("3/5");
                contentText.setText("무게핀을 두 번째 판에 \n연결한 후 다음버튼을 \n눌러주세요.");
                MainActivity.instance.sendData("$c0;");
                break;
            case 3:
                levelText.setText("4/5");
                contentText.setText("무게핀을 세 번째 판에 \n연결한 후 다음버튼을 \n눌러주세요.");
                MainActivity.instance.sendData("$c1;");
                break;
            case 4:
                levelText.setText("5/5");
                contentText.setText("설정 버튼을 누르시면\n캘리브레이션이 완료됩니다.");
                ok.setText("설정");
                MainActivity.instance.sendData("$c2;");
                break;
            case 5:
                MainActivity.instance.sendData("$ce;");
                finish();
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()== MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }
}
