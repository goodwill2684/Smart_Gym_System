package com.jicode.smartgymsystem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.jicode.smartgymsystem.common.BluetoothDeviceManager;
import com.jicode.smartgymsystem.common.ToastUtil;
import com.jicode.smartgymsystem.event.CallbackDataEvent;
import com.jicode.smartgymsystem.event.ConnectEvent;
import com.jicode.smartgymsystem.event.NotifyDataEvent;
import com.vise.baseble.ViseBle;
import com.vise.baseble.common.ConnectState;
import com.vise.baseble.common.PropertyType;
import com.vise.baseble.core.DeviceMirror;
import com.vise.baseble.model.BluetoothLeDevice;
import com.vise.baseble.model.resolver.GattAttributeResolver;
import com.vise.baseble.utils.BleUtil;
import com.vise.baseble.utils.HexUtil;
import com.vise.log.ViseLog;
import com.vise.log.inner.LogcatTree;
import com.vise.xsnow.cache.SpCache;
import com.vise.xsnow.event.BusManager;
import com.vise.xsnow.event.Subscribe;
import com.vise.xsnow.permission.OnPermissionCallback;
import com.vise.xsnow.permission.PermissionManager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String LIST_NAME = "NAME";
    private static final String LIST_UUID = "UUID";
    public static final String WRITE_CHARACTERISTI_UUID_KEY = "write_uuid_key";
    public static final String NOTIFY_CHARACTERISTIC_UUID_KEY = "notify_uuid_key";
    public static final String WRITE_DATA_KEY = "write_data_key";

    public static final int REQUEST_DEVICE = 10;
    private SimpleExpandableListAdapter simpleExpandableListAdapter;

    String dataArray = "";


    private SpCache mSpCache;
    private BluetoothLeDevice mDevice;
    private StringBuilder mOutputInfo = new StringBuilder();
    private List<BluetoothGattService> mGattServices = new ArrayList<>();
    private List<List<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<>();

    boolean isconnected = false;

    ImageView ledView;
    ImageView connect;

    TextView dust1;
    TextView dust2;
    TextView dust3;
    TextView connectid;

    TextView temp;
    TextView humi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        ViseLog.getLogConfig().configAllowLog(true);
        ViseLog.plant(new LogcatTree());
        BluetoothDeviceManager.getInstance().init(this);
        BusManager.getBus().register(this);

        ledView = (ImageView)findViewById(R.id.led);
        connect = (ImageView)findViewById(R.id.connectbtn);

        dust1 = (TextView)findViewById(R.id.dust1);
        dust2 = (TextView)findViewById(R.id.dust2);
        dust3 = (TextView)findViewById(R.id.dust3);
        connectid = (TextView)findViewById(R.id.connectid);

        temp = (TextView)findViewById(R.id.temp);
        humi = (TextView)findViewById(R.id.humi);

        Bitmap bit;
        Canvas canvas;
        Paint paint = new Paint();
        bit = Bitmap.createBitmap(50,50,Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bit);
        canvas.drawColor(Color.WHITE);
        ledView.setImageBitmap(bit);
        paint.setColor(Color.RED);
        canvas.drawCircle(25,25,15,paint);

        dust1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mSpCache.put(WRITE_DATA_KEY + mDevice.getAddress(), "41");
                BluetoothDeviceManager.getInstance().write(mDevice, HexUtil.decodeHex("41".toCharArray()));
            }
        });
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,DeviceScanActivity.class);
                startActivityForResult(intent,REQUEST_DEVICE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_DEVICE)
        {
            if(resultCode == RESULT_OK)
            {
                mDevice = data.getParcelableExtra("extra_device");
                if (mDevice != null) {
                }

                if (!BluetoothDeviceManager.getInstance().isConnected(mDevice)) {
                    BluetoothDeviceManager.getInstance().connect(mDevice);
                    invalidateOptionsMenu();
                }
                mSpCache = new SpCache(this);

            }
        }
    }

    @Subscribe
    public void showConnectedDevice(ConnectEvent event) {
        if (event != null) {
            if (event.isSuccess()) {

                ToastUtil.showToast(MainActivity.this, "Connect Success!");
                connectid.setText(mDevice.getName());
                invalidateOptionsMenu();
                if (event.getDeviceMirror() != null && event.getDeviceMirror().getBluetoothGatt() != null) {
                    simpleExpandableListAdapter = displayGattServices(event.getDeviceMirror().getBluetoothGatt().getServices());
                }

                try {

                    final BluetoothGattService service = mGattServices.get(2);
                    final BluetoothGattCharacteristic characteristic = mGattCharacteristics.get(2).get(0);
                    //         final int charaProp = characteristic.getProperties();

                    mSpCache.put(NOTIFY_CHARACTERISTIC_UUID_KEY + mDevice.getAddress(), characteristic.getUuid().toString());
                    BluetoothDeviceManager.getInstance().bindChannel(mDevice, PropertyType.PROPERTY_NOTIFY, service.getUuid(), characteristic.getUuid(), null);
                    BluetoothDeviceManager.getInstance().registerNotify(mDevice, false);

                    final BluetoothGattService service2 = mGattServices.get(2);
                    final BluetoothGattCharacteristic characteristic2 = mGattCharacteristics.get(2).get(1);
                    mSpCache.put(WRITE_CHARACTERISTI_UUID_KEY + mDevice.getAddress(), characteristic2.getUuid().toString());
                    BluetoothDeviceManager.getInstance().bindChannel(mDevice, PropertyType.PROPERTY_WRITE, service2.getUuid(), characteristic2.getUuid(), null);
                }catch (Exception e)
                {
                    BluetoothDeviceManager.getInstance().disconnect(mDevice);
                    invalidateOptionsMenu();
                }
            } else {
                if (event.isDisconnected()) {
                    ToastUtil.showToast(MainActivity.this, "Disconnect!");
                } else {
                    ToastUtil.showToast(MainActivity.this, "Connect Failure!");
                }
                connectid.setText("No Device");
                invalidateOptionsMenu();
            }
        }
    }

    @Subscribe
    public void showDeviceCallbackData(CallbackDataEvent event) {
        if (event != null) {
            if (event.isSuccess()) {
                if (event.getBluetoothGattChannel() != null && event.getBluetoothGattChannel().getCharacteristic() != null
                        && event.getBluetoothGattChannel().getPropertyType() == PropertyType.PROPERTY_READ) {
                }
            } else {
            }
        }
    }

    @Subscribe
    public void showDeviceNotifyData(NotifyDataEvent event) {
        if (event != null && event.getData() != null && event.getBluetoothLeDevice() != null
                && event.getBluetoothLeDevice().getAddress().equals(mDevice.getAddress())) {
            if(event.getData().length > 0) {
                try {
                    dataArray += new String(event.getData());
                    if (dataArray.length() == 28) {
                        if (dataArray.substring(0, 1).equals("P")) {
                            long checkSum = Integer.parseInt(dataArray.substring(dataArray.length() - 2));
                            dataArray = dataArray.substring(1, dataArray.length() - 2);
                            char c[] = dataArray.toCharArray();
                            long currCheckSum = 0;
                            for (int i = 0; i < c.length; i++) {
                                currCheckSum += c[i];
                            }
                            if (make_ASCII(currCheckSum & 0xf) == checkSum) {
                                dataArray = dataArray.substring(0, dataArray.length() - 1);
                                String dataText[] = dataArray.split(",");
                                String format = ""+Integer.parseInt(dataText[0]);
                                dust1.setText(format);
                                format = ""+Integer.parseInt(dataText[1]);
                                dust2.setText(format);
                                format = ""+Integer.parseInt(dataText[2]);
                                dust3.setText(format);

                                format = ""+Integer.parseInt(dataText[3]);
                                temp.setText(format);
                                format = ""+Integer.parseInt(dataText[4]);
                                humi.setText(format);

                                dataArray = "";
                                if(Integer.parseInt(dataText[1])>300)
                                {
                                    Bitmap bit;
                                    Canvas canvas;
                                    Paint paint = new Paint();
                                    bit = Bitmap.createBitmap(50,50,Bitmap.Config.ARGB_8888);
                                    canvas = new Canvas(bit);
                                    canvas.drawColor(Color.WHITE);
                                    ledView.setImageBitmap(bit);
                                    paint.setColor(Color.RED);
                                    canvas.drawCircle(25,25,15,paint);
                                }else
                                {
                                    Bitmap bit;
                                    Canvas canvas;
                                    Paint paint = new Paint();
                                    bit = Bitmap.createBitmap(50,50,Bitmap.Config.ARGB_8888);
                                    canvas = new Canvas(bit);
                                    canvas.drawColor(Color.WHITE);
                                    ledView.setImageBitmap(bit);
                                    paint.setColor(Color.GRAY);
                                    canvas.drawCircle(25,25,15,paint);
                                }
                            }
                        }
                    } else if (dataArray.length() > 28) {
                        dataArray = "";
                    }
                }catch (Exception e)
                {

                }
            }

        }
    }

    long make_ASCII(long num)
    {
        if(num >= 0 && num <= 9)  num += '0'; // 0 ~ 9
        else              num += '7'; // A ~ F

        return num;
    }
    @Override
    protected void onResume() {
        invalidateOptionsMenu();
        super.onResume();
        checkBluetoothPermission();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.connect, menu);
        if (BluetoothDeviceManager.getInstance().isConnected(mDevice)) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
            connectid.setText(mDevice.getName());
            DeviceMirror deviceMirror = ViseBle.getInstance().getDeviceMirror(mDevice);
            if (deviceMirror != null) {
                simpleExpandableListAdapter = displayGattServices(deviceMirror.getBluetoothGatt().getServices());
            }
            mOutputInfo = new StringBuilder();


        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
            connectid.setText("No Device");
        }
        if (ViseBle.getInstance().getConnectState(mDevice) == ConnectState.CONNECT_PROCESS) {
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_progress_indeterminate);
        } else {
            menu.findItem(R.id.menu_refresh).setActionView(null);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        ViseBle.getInstance().clear();
        BusManager.getBus().unregister(this);
        super.onDestroy();
    }

    private void checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //校验是否已具有模糊定位权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                PermissionManager.instance().with(this).request(new OnPermissionCallback() {
                    @Override
                    public void onRequestAllow(String permissionName) {
                        enableBluetooth();
                    }

                    @Override
                    public void onRequestRefuse(String permissionName) {
                        finish();
                    }

                    @Override
                    public void onRequestNoAsk(String permissionName) {
                        finish();
                    }
                }, Manifest.permission.ACCESS_COARSE_LOCATION);
            } else {
                enableBluetooth();
            }
        } else {
            enableBluetooth();
        }
    }
    private void enableBluetooth() {
        if (!BleUtil.isBleEnable(this)) {
            BleUtil.enableBluetooth(this, 1);
        } else {
        }
    }

    private SimpleExpandableListAdapter displayGattServices(final List<BluetoothGattService> gattServices) {
        if (gattServices == null) return null;
        String uuid;
        final String unknownServiceString = getResources().getString(R.string.unknown_service);
        final String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        final List<Map<String, String>> gattServiceData = new ArrayList<>();
        final List<List<Map<String, String>>> gattCharacteristicData = new ArrayList<>();

        mGattServices = new ArrayList<>();
        mGattCharacteristics = new ArrayList<>();

        // Loops through available GATT Services.
        for (final BluetoothGattService gattService : gattServices) {
            final Map<String, String> currentServiceData = new HashMap<>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME, GattAttributeResolver.getAttributeName(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            final List<Map<String, String>> gattCharacteristicGroupData = new ArrayList<>();
            final List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            final List<BluetoothGattCharacteristic> charas = new ArrayList<>();

            // Loops through available Characteristics.
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                final Map<String, String> currentCharaData = new HashMap<>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(LIST_NAME, GattAttributeResolver.getAttributeName(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }

            mGattServices.add(gattService);
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        final SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(this, gattServiceData, android.R.layout
                .simple_expandable_list_item_2, new String[]{LIST_NAME, LIST_UUID}, new int[]{android.R.id.text1, android.R.id.text2},
                gattCharacteristicData, android.R.layout.simple_expandable_list_item_2, new String[]{LIST_NAME, LIST_UUID}, new
                int[]{android.R.id.text1, android.R.id.text2});
        return gattServiceAdapter;
    }

    private boolean isHexData(String str) {
        if (str == null) {
            return false;
        }
        char[] chars = str.toCharArray();
        if ((chars.length & 1) != 0) {//个数为奇数，直接返回false
            return false;
        }
        for (char ch : chars) {
            if (ch >= '0' && ch <= '9') continue;
            if (ch >= 'A' && ch <= 'F') continue;
            if (ch >= 'a' && ch <= 'f') continue;
            return false;
        }
        return true;
    }
}
