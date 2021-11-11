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


public class Callibration_Plate_Popup extends Activity {
    Button ok;
    TextView levelText,contentText;
    int index = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_plate_callibration);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ok=(Button)findViewById(R.id.ok);
        levelText = findViewById(R.id.level);
        contentText = findViewById(R.id.contenttext);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClose(view);
            }
        });
    }


    public void mOnClose(View v){
        switch(index)
        {
            case 0:
                ok.setText("다음");
                levelText.setText("2/5");
                contentText.setText("10KG을 올리고 다음버튼을 눌러주세요.");
                MainActivity.instance.sendData("$c;");
                break;
            case 1:
                levelText.setText("3/5");
                contentText.setText("20KG을 올리고 다음버튼을 눌러주세요.");
                MainActivity.instance.sendData("$c10;");
                break;
            case 2:
                levelText.setText("4/5");
                contentText.setText("30KG을 올리고 다음버튼을 눌러주세요.");
                MainActivity.instance.sendData("$c20;");
                break;
            case 3:
                levelText.setText("5/5");
                ok.setText("설정");
                contentText.setText("설정 버튼을 누르시면\n캘리브레이션이 완료됩니다.");
                MainActivity.instance.sendData("$c30;");
                break;
            case 4:
                MainActivity.instance.sendData("$ce;");
                finish();
                break;
        }
        index++;
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

}
