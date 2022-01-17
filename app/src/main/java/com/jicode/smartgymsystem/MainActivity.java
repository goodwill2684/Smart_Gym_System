package com.jicode.smartgymsystem;


import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jicode.smartgymsystem.Fragment.LogFragment;
import com.jicode.smartgymsystem.Fragment.CalibrationFragment;
import com.jicode.smartgymsystem.Fragment.MainFragment;
import com.jicode.smartgymsystem.Fragment.SettingFragment;
import com.jicode.smartgymsystem.Lib.BackPressHandler;
import com.jicode.smartgymsystem.Lib.JCSharingPreferences;
import com.jicode.smartgymsystem.Popup.Callibration_PinLoad_Popup;
import com.jicode.smartgymsystem.comm.ObserverManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_OPEN_GPS = 1;
    private static final int REQUEST_CODE_PERMISSION_LOCATION = 2;

    String nfcMac = "";
    private Animation operatingAnim;
    private ProgressDialog progressDialog;

    private BackPressHandler backPressHandler = new BackPressHandler(this);

    public static final String Error_Detected = "NFC 태그를 감지하지 못했습니다.";
    public static final String Write_Success = "성공적으로 NFC에 저장하였습니다.";
    public static final String Write_Error = "쓰는동안 오류가 발생했습니다. 다시 시도해주세요.";
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writingTagFilters[];
    boolean writeMode;
    Tag myTag;
    Context context;
    public static MainActivity instance;
    MainFragment realTimeFragment = null;
    LogFragment logFragment = null;

    BluetoothGattCharacteristic currCharacter = null;
    BluetoothGattCharacteristic writeCharacter = null;

    JCSharingPreferences preferences;

    Handler handler;

    BleDevice device = null;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    public static Fragment fragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_main);
        initView();
        context = this;
        instance = this;
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);
        preferences = new JCSharingPreferences(this);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "이 장치는 NFC를 지원하지 않습니다", Toast.LENGTH_LONG).show();
            preferences.putKey("isDeviceNfc",false);
//            finish();
        }else
        {
            preferences.putKey("isDeviceNfc",true);
        }
        readFromIntent(getIntent());
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writingTagFilters = new IntentFilter[]{tagDetected};

        BottomNavigate(R.id.bottom_map);
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());
    } // OnCreate 끝

    private void readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            buildTagViews(msgs);
        }
    }
    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            BottomNavigate(menuItem.getItemId());
            return true;
        }
    }
    public void BottomNavigate(int id) {  //BottomNavigation 페이지 변경
        String tag = String.valueOf(id);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment currentFragment = fragmentManager.getPrimaryNavigationFragment();

        fragment = fragmentManager.findFragmentByTag(tag);
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }
        if (fragment == null) {
            if (id == R.id.bottom_map) {
                fragment = new MainFragment();
                realTimeFragment = (MainFragment) fragment;
            } else if (id == R.id.bottom_log){
                fragment = new CalibrationFragment();
            }else if (id == R.id.bottom_event){
                logFragment = new LogFragment();
                fragment = logFragment;
            }else if(id == R.id.bottom_setting) {
                fragment = new SettingFragment();
            }
            fragmentTransaction.add(R.id.framelayout, fragment, tag);
        } else fragmentTransaction.show(fragment);

        fragmentTransaction.setPrimaryNavigationFragment(fragment);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commitNow();
    }

    private void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return;
        String text = "";
        String tagId = new String(msgs[0].getRecords()[0].getType());
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";  // 텍스트 인코딩
        int languageCodeLength = payload[0] & 0063; // 언어 코드 , e.g. "en"
        // String languageCode = new String(payload,1,languageCodeLength, "US-ASCII");
        try {
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("UnsupportedEncoding", e.toString());
        }
        //읽었을때 텍스트뷰에 해당 태그의 값 보여줌.

        if (text.toString() != null){
            nfcMac = text;
            checkPermissions();
            BleManager.getInstance().disconnectAllDevice();
        }
    }

    private void write(String text, Tag tag) throws IOException, FormatException {
        NdefRecord[] records = {createRecord(text)};
        NdefMessage message = new NdefMessage(records);
        // 태그에 대한 Ndef의 인스턴스를 가져오기.
        Ndef ndef = Ndef.get(tag);
        // 입출력 활성화
        ndef.connect();
        // Write the message 메세지 쓰기
        ndef.writeNdefMessage(message);
        //close the connection
        ndef.close();
    }

    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength = textBytes.length;
        byte[] payload = new byte[1 + langLength + textLength];
        // 상태 바이트 설정(실제 비트는 NDEF 사양 참조)
        payload[0] = (byte) langLength;
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);

        return recordNFC;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        readFromIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if(preferences.getValue("isDeviceNfc",false))
        WriteModeOff();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(preferences.getValue("isDeviceNfc",false))
            WriteModeOn();
    }
    private void WriteModeOn(){
        writeMode = true;
        nfcAdapter.enableForegroundDispatch(this,pendingIntent,writingTagFilters,null);

    }
    private void WriteModeOff(){
        writeMode =false;
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
    }


    public void initView() {

        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        operatingAnim.setInterpolator(new LinearInterpolator());
        progressDialog = new ProgressDialog(this);
    }

    private void setScanRule() {
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
//                .setDeviceMac(nfcMac)                  // 지정된 mac의 장치만 스캔(선택 사항)
                .setScanTimeOut(10000)              // 스캔 시간 초과 시간, 선택 사항, 기본 10초
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                realTimeFragment.img_loading.startAnimation(operatingAnim);
                realTimeFragment.img_loading.setVisibility(View.VISIBLE);
                Log.d("scan", "start");
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                if(bleDevice.getName() != null)
                Log.d("scan", bleDevice.getName());
                if(bleDevice.getMac().equals(nfcMac)) {
                    connect(bleDevice);
                    Log.d("scan", "find");
                }
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                realTimeFragment.img_loading.clearAnimation();
                realTimeFragment.img_loading.setVisibility(View.INVISIBLE);
                Log.d("scan","Finish");
            }
        });
    }

    private void connect(final BleDevice bleDevice) {
        realTimeFragment.initText();
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                progressDialog.show();
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                realTimeFragment.disconnect.setVisibility(View.INVISIBLE);
                realTimeFragment.img_loading.clearAnimation();
                realTimeFragment.img_loading.setVisibility(View.INVISIBLE);
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, getString(R.string.connect_fail), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                //연결성공
                realTimeFragment.disconnect.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
                realTimeFragment.img_loading.clearAnimation();
                realTimeFragment.img_loading.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, getString(R.string.connect), Toast.LENGTH_LONG).show();
                realTimeFragment.nameText.setText(bleDevice.getName());
                //BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
                for(BluetoothGattService serviceItem : gatt.getServices())
                {
                    for(BluetoothGattCharacteristic characteristic : serviceItem.getCharacteristics())
                    {
                        int charaProp = characteristic.getProperties();
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                                        BleManager.getInstance().notify(
                                                bleDevice,
                                                characteristic.getService().getUuid().toString(),
                                                characteristic.getUuid().toString(),
                                                new BleNotifyCallback() {
                                                    @Override
                                                    public void onNotifySuccess() {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(getApplication(), "notiStart", Toast.LENGTH_SHORT).show();
                                                                currCharacter = characteristic;
                                                                device = bleDevice;
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onNotifyFailure(final BleException exception) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(getApplication(), exception.toString(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onCharacteristicChanged(byte[] data) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                String datastr = new String(characteristic.getValue());
                                                                if(datastr.contains("$r")) {
                                                                    realTimeFragment.plusCount(Integer.parseInt(datastr.substring(2,datastr.indexOf(";"))));
                                                                }else if(datastr.contains("$derr"))
                                                                {
                                                                    Toast.makeText(getApplication(), "캘리브레이션을 다시 해주세요!", Toast.LENGTH_SHORT).show();
                                                                }else if(datastr.contains("$d"))
                                                                {
                                                                    if(Callibration_PinLoad_Popup.instance != null)
                                                                        Callibration_PinLoad_Popup.instance.setDist(datastr.substring(2,datastr.indexOf(";")));
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                    }else if((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0)
                        {
                            writeCharacter = characteristic;
                        }

                        }
                    }
                }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                try {
                    realTimeFragment.disconnect.setVisibility(View.INVISIBLE);
                    progressDialog.dismiss();
                    realTimeFragment.nameText.setText("Device Name");
                    realTimeFragment.initText();
                    if (isActiveDisConnected) {
                        Toast.makeText(MainActivity.this, getString(R.string.active_disconnected), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.disconnected), Toast.LENGTH_LONG).show();
                        ObserverManager.getInstance().notifyObserver(bleDevice);
                    }

                    if (currCharacter != null) {
                        BleManager.getInstance().stopNotify(
                                bleDevice,
                                currCharacter.getService().getUuid().toString(),
                                currCharacter.getUuid().toString());
                        currCharacter = null;
                        writeCharacter = null;
                        device = null;
                    }
                }catch (Exception e)
                {

                }

            }
        });
    }
    public void logUpdate()
    {
        if(logFragment != null)
            logFragment.LogUpdate();
    }
    @Override
    public final void onRequestPermissionsResult(int requestCode,
                                                 @NonNull String[] permissions,
                                                 @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_LOCATION:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            onPermissionGranted(permissions[i]);
                        }
                    }
                }
                break;
        }
    }

    private void checkPermissions() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, getString(R.string.please_open_blue), Toast.LENGTH_LONG).show();
            return;
        }

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, REQUEST_CODE_PERMISSION_LOCATION);
        }
    }

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.notifyTitle)
                            .setMessage(R.string.gpsNotifyMsg)
                            .setNegativeButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                            .setPositiveButton(R.string.setting,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
                                        }
                                    })
                            .setCancelable(false)
                            .show();
                } else {
                    setScanRule();
                    startScan();
                }
                break;
        }
    }

    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_GPS) {
            if (checkGPSIsOpen()) {
                setScanRule();
                startScan();
            }
        }
    }

    public void sendData(String str)
    {
        if(device != null && writeCharacter != null) {
            BleManager.getInstance().write(
                    device,
                    writeCharacter.getService().getUuid().toString(),
                    writeCharacter.getUuid().toString(),
                    str.getBytes(),
                    new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                }
                            });
                        }

                        @Override
                        public void onWriteFailure(final BleException exception) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        }
                    });
        }
    }
    @Override
    public void onBackPressed() {
        backPressHandler.onBackPressed();
    }
}
