
#include <EEPROM.h>

#include "MPU9250.h"

MPU9250 mpu;
float cur_x, cur_y, cur_z;
float prev_x, prev_y, prev_z;
float x, y, z;
unsigned long prev_ms = millis();
unsigned long init_ms;
boolean init_stat = false;
boolean est_stat = false;
int HU_CNT, H_CNT, LU_CNT, L_CNT, FU_CNT, F_CNT, U_CNT;

float Mbuf_x[5] = {0,};
float mbuf_x[5] = {0,};
float preM_x = 0;
float prem_x = 0;
boolean max_x_flag = false;
boolean min_x_flag = false;
int Mx_Count = 0;
int mx_Count = 0;
int countFlag_x = false;

float Mbuf_y[5] = {0,};
float mbuf_y[5] = {0,};
float preM_y = 0;
float prem_y = 0;
boolean max_y_flag = false;
boolean min_y_flag = false;
int My_Count = 0;
int my_Count = 0;
int countFlag_y = false;

float Mbuf_z[5] = {0,};
float mbuf_z[5] = {0,};
float preM_z = 0;
float prem_z = 0;
boolean max_z_flag = false;
boolean min_z_flag = false;
int Mz_Count = 0;
int mz_Count = 0;
int countFlag_z = false;

float ch_x, ch_y, ch_z;
boolean chx_init = false;
boolean chy_init = false;
boolean chz_init = false;
String str_x, str_y, str_z;


float cMx, cmx;
float pre_qx, curr_qx, st_qx, ppr_qx, pre_qy, curr_qy, st_qy, ppr_qy, pre_qz, curr_qz, st_qz, ppr_qz;
boolean orient_x = false;
boolean orient_y = false;
boolean orient_z = false;
boolean calib_stat = true;
boolean cnt_check = true;
//-------------------------------------------mpu9250 preset--------------------------------------------------------------------------
#include "HX711.h"
#define LOADCELL_DOUT_PIN         19
#define LOADCELL_SCK_PIN          18
float loadcellValue = 1;
HX711 scale;

float weight;

unsigned long long loadcellVal_1, loadcellVal_2, loadcellVal_3;
boolean measure_stat;
unsigned long scaleVal;
String readVal;
int scale_1, scale_2, scale_3;
int M_yaw, M_pitch, M_roll;
int m_yaw, m_pitch, m_roll;
int curr_yaw,curr_pitch,curr_roll;
//--------------------------------------------------------loadcell preset----------------------------------------------------------
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

BLEServer *pServer = NULL;
BLECharacteristic * pTxCharacteristic;
bool deviceConnected = false;
bool oldDeviceConnected = false;
uint8_t txValue = 0;

String strRx;

#define SERVICE_UUID           "6E400001-B5A3-F393-E0A9-E50E24DCCA9E" // UART service UUID
#define CHARACTERISTIC_UUID_RX "6E400002-B5A3-F393-E0A9-E50E24DCCA9E"
#define CHARACTERISTIC_UUID_TX "6E400003-B5A3-F393-E0A9-E50E24DCCA9E"

class MyServerCallbacks: public BLEServerCallbacks {
    void onConnect(BLEServer* pServer) {
      deviceConnected = true;
    };

    void onDisconnect(BLEServer* pServer) {
      deviceConnected = false;
    }
};

class MyCallbacks: public BLECharacteristicCallbacks {
    void onWrite(BLECharacteristic *pCharacteristic) {
      std::string rxValue = pCharacteristic->getValue();
      strRx = "";
      if (rxValue.length() > 0) {
        Serial.println("*********");
        Serial.print("Received Value: ");
        for (int i = 0; i < rxValue.length(); i++)
          strRx += rxValue[i];

        Serial.println(strRx);
      }
      if (strRx != "") {
        if (strRx.substring(0, 3) == "$c;") {
          calib_stat = true;
          measure_stat = false;
          scale.tare(10);
          Serial.println("cal on");

        }
        else if (strRx.substring(0, 5) == "$c10;") {
          loadcellVal_1 = 0;
          scale.set_scale(1);
          loadcellVal_1 = scale.get_units(10);

          loadcellVal_1 /= 10;
          Serial.println(loadcellVal_1);
          Serial.println("cal 10");
        }

        else if (strRx.substring(0, 5) == "$c20;") {
          loadcellVal_2 = 0;
          for (int i = 0; i < 10; i++) {
            loadcellVal_2 += scale.get_units(1);
          }
          loadcellVal_2 /= 200;
          Serial.println(loadcellVal_2);
          Serial.println("cal 20");
        }
        else if (strRx.substring(0, 5) == "$c30;") {
          loadcellVal_3 = 0;
          for (int i = 0; i < 10; i++) {
            loadcellVal_3 += scale.get_units(1);
          }
          Serial.println(loadcellVal_3);
          loadcellVal_3 /= 300;
        }
        else if (strRx.substring(0, 4) == "$ce;") {
          scaleVal = (loadcellVal_1 + loadcellVal_2 + loadcellVal_3) / 3;
          scale_1 = scaleVal / 1000;
          scale_2 = (scaleVal % 1000) / 100;
          scale_3 = scaleVal % 100;
          Serial.println(scaleVal);
          Serial.println(scale_1);
          Serial.println(scale_2);
          Serial.println(scale_3);
          EEPROM.write(101, scale_1);
          EEPROM.write(102, scale_2);
          EEPROM.write(103, scale_3);
          EEPROM.commit();
          scale.set_scale(scaleVal);
          measure_stat = true;
          Serial.println("cal end");
        }
        else if (strRx.substring(0, 4) == "$wr;") {
          weight = (scale.get_units(1));
          weight /= 10;
          weight = round(weight);
          weight *= 10;
          Serial.println(weight);
          EEPROM.write(104, int(weight));

          EEPROM.commit();
          preM_z = mpu.getLinearAccZ();
          prem_z = mpu.getLinearAccZ();
        }

//        else if (strRx.substring(0, 4) == "$mc;") {
//          calib_stat = false;
//        }
//        else if (strRx.substring(0, 4) == "$hc;") {
//          cnt_check = false;
//        }
//        else if (strRx.substring(0, 4) == "$he;") {
//          cnt_check = true;
//        }
        strRx = "";
      }

    }
};
//--------------------------------------------------------BLE preset----------------------------------------------------------



void setup() {
  Serial.begin(115200);
  EEPROM.begin(512);
  Wire.begin();
  delay(2000);
  scale.begin(LOADCELL_DOUT_PIN, LOADCELL_SCK_PIN);
//  for (int i = 0; i < 256; i++)
//  {
//    Serial.println(EEPROM.read(i));
//  }

  M_yaw = M_pitch = M_roll = -100;
  m_yaw = m_pitch = m_roll = 100;
//  delay(2000);
  if (EEPROM.read(101) != 255) {
    loadcellValue = (EEPROM.read(101)) * 1000 + (EEPROM.read(102)) * 100 + (EEPROM.read(103));
          M_yaw=EEPROM.read(104);
          m_yaw=-(EEPROM.read(105));
          M_pitch=EEPROM.read(106);
          m_pitch=-(EEPROM.read(107));
          M_roll=EEPROM.read(108);
          m_roll=-(EEPROM.read(109));
    Serial.println(EEPROM.read(101));
    Serial.println(EEPROM.read(102));
    Serial.println(EEPROM.read(103));
    Serial.println(EEPROM.read(104));
    Serial.println(EEPROM.read(105));
    Serial.println(EEPROM.read(106));
    Serial.println(EEPROM.read(107));
    Serial.println(EEPROM.read(108));
    Serial.println(EEPROM.read(109));
    Serial.println(loadcellValue);
    scale.set_scale(loadcellValue);//eeprom으로 바꾸기
    calib_stat = true;
    cnt_check = true;
  }
  if (loadcellValue == 0 || loadcellValue == 255) scale.set_scale(1);
  if (!mpu.setup(0x68)) {  // change to your own address
    while (1) {
      Serial.println("MPU connection failed. Please check your connection with `connection_check` example.");
      delay(5000);
    }
  }

  for (int i = 0 ; i < 5 ; i ++)
  {
    mpu.update();
    Mbuf_x[i] = mpu.getLinearAccX();
    mbuf_x[i] = mpu.getLinearAccX();
    preM_x = mpu.getLinearAccX();
    prem_x = mpu.getLinearAccX();
    Mbuf_y[i] = mpu.getLinearAccY();
    mbuf_y[i] = mpu.getLinearAccY();
    preM_y = mpu.getLinearAccY();
    prem_y = mpu.getLinearAccY();
    Mbuf_z[i] = mpu.getLinearAccZ();
    mbuf_z[i] = mpu.getLinearAccZ();
    preM_z = mpu.getLinearAccZ();
    prem_z = mpu.getLinearAccZ();
  }

  BLEDevice::init("Smart_Gym");
  // Create the BLE Server
  pServer = BLEDevice::createServer();
  pServer->setCallbacks(new MyServerCallbacks());

  // Create the BLE Service
  BLEService *pService = pServer->createService(SERVICE_UUID);

  // Create a BLE Characteristic
  pTxCharacteristic = pService->createCharacteristic(
                        CHARACTERISTIC_UUID_TX,
                        BLECharacteristic::PROPERTY_NOTIFY
                      );

  pTxCharacteristic->addDescriptor(new BLE2902());

  BLECharacteristic * pRxCharacteristic = pService->createCharacteristic(
      CHARACTERISTIC_UUID_RX,
      BLECharacteristic::PROPERTY_WRITE
                                          );

  pRxCharacteristic->setCallbacks(new MyCallbacks());

  // Start the service
  pService->start();

  // Start advertising
  pServer->getAdvertising()->start();
  Serial.println("Waiting a client connection to notify...");

  ch_x = ch_y = ch_z = 100;
}


void loop() {

  setUp();
  for (int i = 0; i < 10; i++) {
    mpu.update();
    for (int j = 0; j < 5; j++) {


      if (Mbuf_x[j] < mpu.getLinearAccX())
      {
        if (j < 4) {
          Mbuf_x[j + 1] = Mbuf_x[j];
          Mbuf_x[j] = mpu.getLinearAccX();
        }
        else {
          Mbuf_x[j] = mpu.getLinearAccX();
        }
      }
      else if (mbuf_x[j] > mpu.getLinearAccX()) {
        if (j < 4) {
          mbuf_x[j + 1] = Mbuf_x[j];
          mbuf_x[j] = mpu.getLinearAccX();
        }
        else {
          mbuf_x[j] = mpu.getLinearAccX();
        }
      }


      if (Mbuf_y[j] < mpu.getLinearAccY())
      {
        if (j < 4) {
          Mbuf_y[j + 1] = Mbuf_y[j];
          Mbuf_y[j] = mpu.getLinearAccY();
        }
        else {
          Mbuf_y[j] = mpu.getLinearAccY();
        }
      }
      else if (mbuf_y[j] > mpu.getLinearAccY()) {
        if (j < 4) {
          mbuf_y[j + 1] = Mbuf_y[j];
          mbuf_y[j] = mpu.getLinearAccY();
        }
        else {
          mbuf_y[j] = mpu.getLinearAccY();
        }
      }

      if (Mbuf_z[j] < mpu.getLinearAccZ())
      {
        if (j < 4) {
          Mbuf_z[j + 1] = Mbuf_z[j];
          Mbuf_z[j] = mpu.getLinearAccZ();
        }
        else {
          Mbuf_z[j] = mpu.getLinearAccZ();
        }
      }
      else if (mbuf_z[j] > mpu.getLinearAccZ()) {
        if (j < 4) {
          mbuf_z[j + 1] = Mbuf_z[j];
          mbuf_z[j] = mpu.getLinearAccZ();
        }
        else {
          mbuf_z[j] = mpu.getLinearAccZ();
        }
      }
    }
    delay(1);
  }

  curr_yaw = mpu.getYaw();
  curr_pitch = mpu.getPitch();
  curr_roll = mpu.getRoll();
  if (!calib_stat) {
    M_yaw = max(M_yaw,curr_yaw);
    M_pitch = max(M_pitch, curr_pitch);
    M_roll = max(M_roll, curr_roll);
    m_yaw = min(m_yaw, curr_yaw);
    m_pitch = min(m_pitch, curr_pitch);
    m_roll = min(m_roll, curr_roll);
  }

  AXIS_X();
  AXIS_Y();
  AXIS_Z();


  if (!deviceConnected && oldDeviceConnected) {
    delay(500); // give the bluetooth stack the chance to get things ready
    pServer->startAdvertising(); // restart advertising
    Serial.println("start advertising");
    oldDeviceConnected = deviceConnected;
  }
  // connecting

  if (deviceConnected && !oldDeviceConnected) {
    // do stuff here on connecting
    oldDeviceConnected = deviceConnected;
  }

}


void setUp() {
  if (Mx_Count > 10) {
    Mx_Count = 0;
    max_x_flag = false;
  }
  if (mx_Count > 10) {
    mx_Count = 0;
    min_x_flag = false;
  }
  if (My_Count > 10) {
    My_Count = 0;
    max_y_flag = false;
  }
  if (my_Count > 10) {
    my_Count = 0;
    min_y_flag = false;
  }
  if (Mz_Count > 10) {
    Mz_Count = 0;
    max_z_flag = false;
  }
  if (mz_Count > 10) {
    mz_Count = 0;
    min_z_flag = false;
  }

  for (int i = 0; i < 5; i++) {
    Mbuf_x[i] = 0;
    mbuf_x[i] = 100;
    Mbuf_y[i] = 0;
    mbuf_y[i] = 100;
    Mbuf_z[i] = 0;
    mbuf_z[i] = 100;
  }
}

void AXIS_X() {
  if (pre_qx != 0 && ppr_qx != pre_qx) {
    ppr_qx = pre_qx;
  }
  if (curr_qx != 0 && pre_qx != curr_qx) {
    pre_qx = curr_qx;
  }
  curr_qx = mpu.getQuaternionX();
  if (curr_qx > pre_qx) {
    if (pre_qx > ppr_qx)orient_x = true;
    else if (curr_qx == ppr_qx) orient_x = false;
  }
  else if (curr_qx <= pre_qx) orient_x = false;
  float eux = mpu.getGyroX();


  float currMax_x = 0;
  float currMin_x = 0;

  for (int i = 0; i < 5; i++) {
    currMax_x += Mbuf_x[i];
    currMin_x += mbuf_x[i];
  }

  currMax_x = currMax_x / 5;
  currMin_x = currMin_x / 5;

  if (currMin_x - prem_x < -(0.15)) {
    min_x_flag = true;
    mx_Count = 0;
  }

  str_x = "";
  if (currMax_x - preM_x > (0.13) && currMax_x - preM_x < (0.8)) {
    if (!max_x_flag && !min_x_flag) {
      countFlag_x = true;
    }
    else if (Mx_Count == 1) {
      if (countFlag_x) {
        if (orient_x) {
//          if (curr_yaw < M_yaw+7 && curr_yaw > m_yaw-7 && curr_pitch < M_pitch+7 && curr_pitch > m_pitch-7 && curr_roll < M_roll+7 && curr_roll > m_roll-7) {
            //          if(eux<-2){
            //          if(curr_qx-st_qx>4){
            Serial.println("COUNT3!!");
            if (EEPROM.read(104) != 255) {
              weight = EEPROM.read(104);
              Serial.println(weight);
            }
            else weight = 0;
            if (calib_stat) {
              if (cnt_check) {
                str_x = "$r" + String(int(weight)) + ";";
              }
              else {
                str_x = "$ch;";
              }
            }
            else {
          EEPROM.write(104, M_yaw);
          EEPROM.write(105, -m_yaw);
          EEPROM.write(106, M_pitch);
          EEPROM.write(107, -m_pitch);
          EEPROM.write(108, M_roll);
          EEPROM.write(109, -m_roll);
              str_x = "$mr;";
              calib_stat = true;
          EEPROM.commit();
            }
            pTxCharacteristic->setValue(str_x.c_str());
            pTxCharacteristic->notify();
            delay(10);
            min_x_flag = true;
            min_y_flag = true;
            min_z_flag = true;
            stay();
            //          }
            //          }
//          }
        }
      }
    }
    max_x_flag = true;
    Mx_Count = 0;
  }
  else {
    countFlag_x = false;
  }
  if (!chx_init) {
    ch_x = currMax_x - preM_x;
    chx_init = true;
  }
  str_x = String(currMax_x - preM_x);
  if (max_x_flag)Mx_Count++;
  else Mx_Count = 0;
  if (min_x_flag)mx_Count++;
  else mx_Count = 0;

}



void AXIS_Y() {
  if (pre_qy != 0 && ppr_qy != pre_qy) {
    ppr_qy = pre_qy;
  }
  if (curr_qy != 0 && pre_qy != curr_qy) {
    pre_qy = curr_qy;
  }
  curr_qy = mpu.getQuaternionY();
  if (curr_qy > pre_qy) {
    if (pre_qy > ppr_qy)orient_y = true;
    else if (curr_qy == ppr_qy) orient_y = false;
  }
  else if (curr_qy <= pre_qy) orient_y = false;
  float euy = mpu.getGyroY();

  float currMax_y = 0;
  float currMin_y = 0;

  for (int i = 0; i < 5; i++) {
    currMax_y += Mbuf_y[i];
    currMin_y += mbuf_y[i];
  }

  currMax_y = currMax_y / 5;
  currMin_y = currMin_y / 5;

  if (currMin_y - prem_y < -(0.15)) {
    min_y_flag = true;
    my_Count = 0;
  }

  str_y = "";
  if (currMax_y - preM_y > (0.13) && currMax_y - preM_y < (0.8)) {
    if (!max_y_flag && !min_y_flag) {
      countFlag_y = true;
    }
    else if (My_Count == 1) {
      if (countFlag_y) {
                if (orient_y) {
//                   if (curr_yaw < M_yaw+7 && curr_yaw > m_yaw-7 && curr_pitch < M_pitch+7 && curr_pitch > m_pitch-7 && curr_roll < M_roll+7 && curr_roll > m_roll-7) {
           
        //          if(euy<-2){
        //          if(curr_qy-st_qy>4){
        Serial.println("COUNT3!!");
        if (EEPROM.read(104) != 255) {
          weight = EEPROM.read(104);
          Serial.println(weight);
        }
        else weight = 0;
        if (calib_stat) {
          if (cnt_check) {
            str_y = "$r" + String(int(weight)) + ";";
          }
          else {
            str_y = "$ch;";
          }
        }
        else {
          str_y = "$mr;";
          calib_stat = true;
        }
        pTxCharacteristic->setValue(str_y.c_str());
        pTxCharacteristic->notify();
        delay(10);
        min_x_flag = true;
        min_y_flag = true;
        min_z_flag = true;
        stay();
                  }
//                }
        //          }
        //        }
      }
    }
    max_y_flag = true;
    My_Count = 0;
  }
  else {
    countFlag_y = false;
  }
  if (!chy_init) {
    ch_y = currMax_y - preM_y;
    chy_init = true;
  }
  str_y = String(currMax_y - preM_y);
  //  Serial.print("str : ");
  //  Serial.println(str);

  if (max_y_flag)My_Count++;
  else My_Count = 0;
  if (min_y_flag)my_Count++;
  else my_Count = 0;

}

void AXIS_Z() {
  if (pre_qz != 0 && ppr_qz != pre_qz) {
    ppr_qz = pre_qz;
  }
  if (curr_qz != 0 && pre_qz != curr_qz) {
    pre_qz = curr_qz;
  }
  curr_qz = mpu.getQuaternionZ();
  if (curr_qz > pre_qz) {
    if (pre_qz > ppr_qz)orient_z = true;
    else if (curr_qz == ppr_qz) orient_z = false;
  }
  else if (curr_qz <= pre_qz) orient_z = false;
  float euz = mpu.getGyroZ();
  float currMax_z = 0;
  float currMin_z = 0;

  for (int i = 0; i < 5; i++) {
    currMax_z += Mbuf_z[i];
    currMin_z += mbuf_z[i];
  }

  currMax_z = currMax_z / 5;
  currMin_z = currMin_z / 5;

  if (currMin_z - prem_z < -(0.15)) {
    min_z_flag = true;
    mz_Count = 0;
  }

  str_z = "";
  if (currMax_z - preM_z > (0.13) && currMax_z - preM_z < (0.8)) {
    if (!max_z_flag && !min_z_flag) {
      countFlag_z = true;
    }
    else if (Mz_Count == 1) {
      if (countFlag_z) {
                if (orient_z) {
//                   if ((curr_yaw > M_yaw-5 || curr_yaw < m_yaw+5) && (curr_pitch > M_pitch-5 || curr_pitch < m_pitch+5) && (curr_roll > M_roll-5 || curr_roll < m_roll+5)) {
          
          
        //          if(euz<-2){
        //          if(curr_qz-st_qz>4){
        Serial.println("COUNT3!!");
        if (EEPROM.read(104) != 255) {
          weight = EEPROM.read(104);
          Serial.println(weight);
        }
        else weight = 0;
        if (calib_stat) {
          if (cnt_check) {
            str_z = "$r" + String(int(weight)) + ";";
          }
          else {
            str_z = "$ch;";
          }
        }
        else {
          str_z = "$mr;";
          calib_stat = true;
        }
        pTxCharacteristic->setValue(str_z.c_str());
        pTxCharacteristic->notify();
        delay(10);
        min_x_flag = true;
        min_y_flag = true;
        min_z_flag = true;
        stay();
                  }
//                  }
        //        }
      }
    }
    max_z_flag = true;
    Mz_Count = 0;
  }
  else {
    countFlag_z = false;
  }
  if (!chz_init) {
    ch_z = currMax_z - preM_z;
    chz_init = true;
  }
  str_z = String(currMax_z - preM_z);
  //  Serial.print("str : ");
  //  Serial.println(str);

  if (max_z_flag)Mz_Count++;
  else Mz_Count = 0;
  if (min_z_flag)mz_Count++;
  else mz_Count = 0;
}


void stay() {
  unsigned long long delay_time;
  delay_time = millis();

  while (millis() - delay_time < 1000) {
    setUp();
    for (int i = 0; i < 10; i++) {
      mpu.update();
      for (int j = 0; j < 5; j++) {


        if (Mbuf_x[j] < mpu.getLinearAccX())
        {
          if (j < 4) {
            Mbuf_x[j + 1] = Mbuf_x[j];
            Mbuf_x[j] = mpu.getLinearAccX();
          }
          else {
            Mbuf_x[j] = mpu.getLinearAccX();
          }
        }
        else if (mbuf_x[j] > mpu.getLinearAccX()) {
          if (j < 4) {
            mbuf_x[j + 1] = Mbuf_x[j];
            mbuf_x[j] = mpu.getLinearAccX();
          }
          else {
            mbuf_x[j] = mpu.getLinearAccX();
          }
        }


        if (Mbuf_y[j] < mpu.getLinearAccY())
        {
          if (j < 4) {
            Mbuf_y[j + 1] = Mbuf_y[j];
            Mbuf_y[j] = mpu.getLinearAccY();
          }
          else {
            Mbuf_y[j] = mpu.getLinearAccY();
          }
        }
        else if (mbuf_y[j] > mpu.getLinearAccY()) {
          if (j < 4) {
            mbuf_y[j + 1] = Mbuf_y[j];
            mbuf_y[j] = mpu.getLinearAccY();
          }
          else {
            mbuf_y[j] = mpu.getLinearAccY();
          }
        }

        if (Mbuf_z[j] < mpu.getLinearAccZ())
        {
          if (j < 4) {
            Mbuf_z[j + 1] = Mbuf_z[j];
            Mbuf_z[j] = mpu.getLinearAccZ();
          }
          else {
            Mbuf_z[j] = mpu.getLinearAccZ();
          }
        }
        else if (mbuf_z[j] > mpu.getLinearAccZ()) {
          if (j < 4) {
            mbuf_z[j + 1] = Mbuf_z[j];
            mbuf_z[j] = mpu.getLinearAccZ();
          }
          else {
            mbuf_z[j] = mpu.getLinearAccZ();
          }
        }
      }
      delay(1);
    }
  }
}
