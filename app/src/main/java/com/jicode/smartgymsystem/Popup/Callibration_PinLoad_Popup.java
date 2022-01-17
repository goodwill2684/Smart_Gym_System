package com.jicode.smartgymsystem.Popup;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.jicode.smartgymsystem.Lib.JCSharingPreferences;
import com.jicode.smartgymsystem.MainActivity;
import com.jicode.smartgymsystem.R;
import com.jicode.smartgymsystem.databinding.ActivityMyinfomationEditBinding;
import com.jicode.smartgymsystem.databinding.PopupPinCallibrationBinding;


public class Callibration_PinLoad_Popup extends Activity {
    Button ok, cancel;
    TextView levelText,contentText;
    int index = 0;
    int pinCount = 0;
    PopupPinCallibrationBinding binding;
    JCSharingPreferences jcSharingPreferences;
    public static Callibration_PinLoad_Popup instance = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        binding = PopupPinCallibrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        instance = this;
        jcSharingPreferences = new JCSharingPreferences(this);
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
        ++index;
        if(index == 1) {
            ok.setText("다음");
            contentText.setText("중량판의 개수를 입력해주세요.");
            binding.countEdit.setVisibility(View.VISIBLE);
            MainActivity.instance.sendData("$de;");
        }else if(index == 2) {
            if (binding.countEdit.getText().length() > 0) {
                pinCount = Integer.parseInt(binding.countEdit.getText().toString());
                binding.countEdit.setVisibility(View.GONE);
                binding.countEdit.setText("");
                levelText.setVisibility(View.VISIBLE);
                ok.setText("다음");
                levelText.setText(String.valueOf(index - 1) + "/" + String.valueOf(pinCount));
                contentText.setText("무게핀을 " + String.valueOf(index - 1) + "번 판에 \n연결한 후 다음버튼을 \n눌러주세요.");
            } else {
                Toast.makeText(getApplicationContext(), "개수를 입력해주세요.", Toast.LENGTH_SHORT).show();
                index--;
            }
        }else if(index-2 < pinCount) {
            ok.setText("다음");
            levelText.setText(String.valueOf(index - 1) + "/" + String.valueOf(pinCount));
            contentText.setText("무게핀을 " + String.valueOf(index - 1) + "번 판에 \n연결한 후 다음버튼을 \n눌러주세요.");
            MainActivity.instance.sendData("$c"+(index-2)+";");
        }else if(index-2 == pinCount) {
            ok.setText("다음");
            levelText.setVisibility(View.INVISIBLE);
            contentText.setText("판 하나당의 무게를 입력해주세요(KG)");
            binding.countEdit.setVisibility(View.VISIBLE);
            binding.countEdit.setHint("무게 입력(kg)");
            MainActivity.instance.sendData("$c"+(index-2)+";");
        }else if(index-2 == pinCount+1) {
            jcSharingPreferences.putKey("weight",Integer.parseInt(binding.countEdit.getText().toString()));
            binding.countEdit.setVisibility(View.GONE);
            binding.countEdit.setText("");
            contentText.setText("설정 버튼을 누르시면\n캘리브레이션이 완료됩니다.");
            ok.setText("설정");
        }else if(index-2 == pinCount+2) {
            MainActivity.instance.sendData("$ce;");
            finish();
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
