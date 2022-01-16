package com.jicode.smartgymsystem.Fragment;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.clj.fastble.BleManager;
import com.jicode.smartgymsystem.Lib.JCSharingPreferences;
import com.jicode.smartgymsystem.R;
import com.jicode.smartgymsystem.SetMyInfoActivity;
import com.jicode.smartgymsystem.databinding.FragmentLogBinding;
import com.jicode.smartgymsystem.databinding.FragmentSettingBinding;

public class SettingFragment extends Fragment {
    FragmentSettingBinding binding;
    JCSharingPreferences preferences;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        preferences = new JCSharingPreferences(getActivity());

        binding.name.setText(preferences.getValue("name","Name"));
        binding.email.setText(preferences.getValue("email","Email"));
        binding.heightText.setText(preferences.getValue("height","0")+"cm");
        binding.weightText.setText(preferences.getValue("weight","0")+"kg");
        binding.exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BleManager.getInstance().disconnectAllDevice();
                System.exit(0);
            }
        });

        binding.setProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getContext(), SetMyInfoActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.xml.animation1, R.xml.animation2);
            }
        });




        return root;
    }

    @Override
    public void onResume() {
        binding.name.setText(preferences.getValue("name","Name"));
        binding.email.setText(preferences.getValue("email","Email"));
        binding.heightText.setText(preferences.getValue("height","0")+"cm");
        binding.weightText.setText(preferences.getValue("weight","0")+"kg");
        super.onResume();
    }
}
