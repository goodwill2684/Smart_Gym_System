package com.jicode.smartgymsystem.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jicode.smartgymsystem.Popup.Callibration_PinLoad_Popup;
import com.jicode.smartgymsystem.Popup.Callibration_Plate_Popup;
import com.jicode.smartgymsystem.R;
import com.jicode.smartgymsystem.databinding.FragmentCalibrationBinding;

public class CalibrationFragment extends Fragment {

    FragmentCalibrationBinding binding;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentCalibrationBinding.inflate(inflater,container, false);
        View root = binding.getRoot();

        binding.plateload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Callibration_Plate_Popup.class);
                startActivity(intent);
            }
        });
        binding.pinload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Callibration_PinLoad_Popup.class);
                startActivity(intent);
            }
        });
        return root;
    }


}
