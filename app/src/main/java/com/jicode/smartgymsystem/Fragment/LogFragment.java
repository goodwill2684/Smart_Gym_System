package com.jicode.smartgymsystem.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.jicode.smartgymsystem.Popup.Callibration_Plate_Popup;
import com.jicode.smartgymsystem.R;
import com.jicode.smartgymsystem.databinding.FragmentCallBinding;

public class LogFragment extends Fragment {

    FragmentCallBinding binding;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_call, container, false);
        View root = binding.getRoot();

        binding.plateload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Callibration_Plate_Popup.class);
                startActivity(intent);
            }
        });
        return root;
    }


}
