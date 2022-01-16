package com.jicode.smartgymsystem.Popup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import androidx.annotation.Nullable;

import com.jicode.smartgymsystem.R;
import com.jicode.smartgymsystem.databinding.PopupCalendarBinding;

public class CalendarPopup extends Activity {
    PopupCalendarBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = PopupCalendarBinding.inflate(getLayoutInflater());
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }


    public void mOnClose(View v){
        Intent intent = new Intent();
        intent.putExtra("ok",true );
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()== MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

}
