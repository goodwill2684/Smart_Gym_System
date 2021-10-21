package com.jicode.smartgymsystem.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;


import com.jicode.smartgymsystem.R;
import com.vise.baseble.model.BluetoothLeDevice;
import com.vise.xsnow.ui.adapter.helper.HelperAdapter;
import com.vise.xsnow.ui.adapter.helper.HelperViewHolder;


public class DeviceAdapter extends HelperAdapter<BluetoothLeDevice> {

    public DeviceAdapter(Context context) {
        super(context, R.layout.item_scan_layout);
    }

    @Override
    public void HelpConvert(HelperViewHolder viewHolder, int position, BluetoothLeDevice bluetoothLeDevice) {
        TextView deviceNameTv = viewHolder.getView(R.id.device_name);
        TextView deviceMacTv = viewHolder.getView(R.id.device_mac);
        TextView deviceRssiTv = viewHolder.getView(R.id.device_rssi);
        if (bluetoothLeDevice != null && bluetoothLeDevice.getDevice() != null) {
            @SuppressLint("MissingPermission") String deviceName = bluetoothLeDevice.getDevice().getName();
            if (deviceName != null && !deviceName.isEmpty()) {
                deviceNameTv.setText(deviceName);
            } else {
                deviceNameTv.setText("Unknown device");
            }
            deviceMacTv.setText(bluetoothLeDevice.getDevice().getAddress());
            deviceRssiTv.setText("RSSI:" + bluetoothLeDevice.getRssi() + "dB");
        }
    }
}
