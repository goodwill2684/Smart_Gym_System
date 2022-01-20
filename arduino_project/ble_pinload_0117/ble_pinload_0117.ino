#include <EEPROM.h>

#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>
#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_ADXL345_U.h>


#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

BLEServer *pServer = NULL;
BLECharacteristic * pTxCharacteristic;
bool deviceConnected = false;
bool oldDeviceConnected = false;
uint8_t txValue = 0;
String strRx;
// See the following for generating UUIDs:
// https://www.uuidgenerator.net/

#define SERVICE_UUID           "6E400001-B5A3-F393-E0A9-E50E24DCCA9E" // UART service UUID
#define CHARACTERISTIC_UUID_RX "6E400002-B5A3-F393-E0A9-E50E24DCCA9E"
#define CHARACTERISTIC_UUID_TX "6E400003-B5A3-F393-E0A9-E50E24DCCA9E"

unsigned long sendTime = 0;

#include "Adafruit_VL53L0X.h"

Adafruit_VL53L0X lox = Adafruit_VL53L0X();
Adafruit_VL53L0X lox2 = Adafruit_VL53L0X();
// address we will assign if dual sensor is present
#define LOX1_ADDRESS 0x30
#define LOX2_ADDRESS 0x31

// set the pins to shutdown
#define SHT_LOX1 34
#define SHT_LOX2 32

float gydist = 0;

int vldist = 0;

/* Assign a unique ID to this sensor at the same time */
Adafruit_ADXL345_Unified accel = Adafruit_ADXL345_Unified(12345);
float maxBuff[5] = {0,};
float minBuff[5] = {0,};
float preMax = 0;
float preMin = 0;
bool maxflag = false;
bool minflag = false;

int maxCount = 0;
int minCount = 0;

int countFlag = false;

float IRVal_10, IRVal_11, IRVal_12, IR0, IR1;
int IRVal_2, scaleVal, pre_vl, cur_vl;
float weight;
boolean ircnt = false;
boolean accelcnt = false;
boolean ready_check = false;
boolean reset_stat = true;
bool rawd_stat = false;
int ir_delay, accel_delay;

float currMax = 0;
float currMin = 0;

float c_array[25] = {0,};
int c_cnt = 0;
boolean c_stat = false;
void displaySensorDetails(void)
{
  sensor_t sensor;
  accel.getSensor(&sensor);
  Serial.println("------------------------------------");
  Serial.print  ("Sensor:       "); Serial.println(sensor.name);
  Serial.print  ("Driver Ver:   "); Serial.println(sensor.version);
  Serial.print  ("Unique ID:    "); Serial.println(sensor.sensor_id);
  Serial.print  ("Max Value:    "); Serial.print(sensor.max_value); Serial.println(" m/s^2");
  Serial.print  ("Min Value:    "); Serial.print(sensor.min_value); Serial.println(" m/s^2");
  Serial.print  ("Resolution:   "); Serial.print(sensor.resolution); Serial.println(" m/s^2");
  Serial.println("------------------------------------");
  Serial.println("");
  delay(500);
}

void displayDataRate(void)
{
  Serial.print  ("Data Rate:    ");

  switch (accel.getDataRate())
  {
    case ADXL345_DATARATE_3200_HZ:
      Serial.print  ("3200 ");
      break;
    case ADXL345_DATARATE_1600_HZ:
      Serial.print  ("1600 ");
      break;
    case ADXL345_DATARATE_800_HZ:
      Serial.print  ("800 ");
      break;
    case ADXL345_DATARATE_400_HZ:
      Serial.print  ("400 ");
      break;
    case ADXL345_DATARATE_200_HZ:
      Serial.print  ("200 ");
      break;
    case ADXL345_DATARATE_100_HZ:
      Serial.print  ("100 ");
      break;
    case ADXL345_DATARATE_50_HZ:
      Serial.print  ("50 ");
      break;
    case ADXL345_DATARATE_25_HZ:
      Serial.print  ("25 ");
      break;
    case ADXL345_DATARATE_12_5_HZ:
      Serial.print  ("12.5 ");
      break;
    case ADXL345_DATARATE_6_25HZ:
      Serial.print  ("6.25 ");
      break;
    case ADXL345_DATARATE_3_13_HZ:
      Serial.print  ("3.13 ");
      break;
    case ADXL345_DATARATE_1_56_HZ:
      Serial.print  ("1.56 ");
      break;
    case ADXL345_DATARATE_0_78_HZ:
      Serial.print  ("0.78 ");
      break;
    case ADXL345_DATARATE_0_39_HZ:
      Serial.print  ("0.39 ");
      break;
    case ADXL345_DATARATE_0_20_HZ:
      Serial.print  ("0.20 ");
      break;
    case ADXL345_DATARATE_0_10_HZ:
      Serial.print  ("0.10 ");
      break;
    default:
      Serial.print  ("???? ");
      break;
  }
  Serial.println(" Hz");
}

void displayRange(void)
{
  Serial.print  ("Range:         +/- ");

  switch (accel.getRange())
  {
    case ADXL345_RANGE_16_G:
      Serial.print  ("16 ");
      break;
    case ADXL345_RANGE_8_G:
      Serial.print  ("8 ");
      break;
    case ADXL345_RANGE_4_G:
      Serial.print  ("4 ");
      break;
    case ADXL345_RANGE_2_G:
      Serial.print  ("2 ");
      break;
    default:
      Serial.print  ("?? ");
      break;
  }
  Serial.println(" g");
}


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
        if (strRx.substring(0, 4) == "$rd;") {
          ready_check = false;
          reset_stat = false;
          rawd_stat = true;
        }
        else if (strRx.substring(0, 4) == "$de;") {
          rawd_stat = false;
        }



        else if (strRx.substring(0, 4) == "$ce;") {
          scaleVal = IRVal_2 / c_cnt;
          EEPROM.write(101, scaleVal);
          EEPROM.write(102, c_cnt);
          for (int i = 0; i < c_cnt; i++) {
            EEPROM.write((100 + i + 3), int(c_array[i]));
          }
          EEPROM.commit();
          Serial.println("cal end");
          for (int i = 0; i <= c_cnt + 1; i++) {
            Serial.println(EEPROM.read((100 + i + 1)));
          }
          ready_check = true;
        }
        else if (strRx.substring(0, 4) == "$re;") {
          scaleVal = 0;
          IRVal_2 = 0;
          c_cnt = 0;
          for (int i = 0; i < c_cnt; i++) {
            c_array[i] = 0;
          }
          for (int i = 0; i <= c_cnt; i++) {
            EEPROM.write((100 + i + 1), 255);
          }
          EEPROM.commit();
          ready_check = false;
          reset_stat = true;
          rawd_stat = false;
        }
        else {
          for (int i = 0; i < 10; i++) {
            if (strRx.substring(0, 4) == "$c" + String(i) + ";") {
              Serial.print("array");
              for (int j = 0; j < c_cnt; j++) {
                Serial.print(c_array[j]);
                Serial.print(",  ");
              }
              Serial.println("------------------------------");
              c_cnt++;
              c_stat = true;
              Serial.println(i);
            }
          }
          for (int i = 10; i < 25; i++) {
            if (strRx.substring(0, 5) == "$c" + String(i) + ";") {
              Serial.print("array");
              for (int j = 0; j < c_cnt; j++) {
                Serial.print(c_array[j]);
                Serial.print(",  ");
              }
              Serial.println("------------------------------");
              c_cnt++;
              c_stat = true;
              Serial.println(i);
            }
          }

        }

        strRx = "";
      }
    }
};
//................................................................................................
void setup() {
  Serial.begin(115200);
  EEPROM.begin(512);
  while (! Serial) {
    delay(1);
  }
  if (EEPROM.read(101) != 255) {
    scaleVal = EEPROM.read(101);
    c_cnt = EEPROM.read(102);
    for (int i = 0; i < c_cnt; i++) {
      c_array[i] = EEPROM.read((100 + i + 3));
    }
    for (int i = 0; i <= c_cnt + 1; i++) {
      Serial.println(EEPROM.read((100 + i + 1)));
    }
    ready_check = true;
    reset_stat = false;
  }
  // wait until serial port opens for native USB devices
  while (! Serial) {
    delay(1);
  }

  pinMode(SHT_LOX1, OUTPUT);
  pinMode(SHT_LOX2, OUTPUT);

  Serial.println(F("Shutdown pins inited..."));

  digitalWrite(SHT_LOX1, LOW);
  digitalWrite(SHT_LOX2, LOW);

  Serial.println(F("Both in reset mode...(pins are low)"));


  Serial.println(F("Starting..."));


  BLE_SET();
  ACCEL_SET();
  setID();
  //  IR_SET();
}
//...........................................................................................................
void loop() {
  String str;
  if (ready_check) {
    ACCEL_ME(str);
    pre_vl = vldist;
    IR_ME();
    cur_vl = vldist;
    Serial.print(currMax);
    Serial.print(", ");
    Serial.print(currMin);
    Serial.println(", ");
    //    Serial.println(scaleVal);
    //    Serial.print(", ");
    Serial.println(gydist);
    //    Serial.print(", ");
    //    Serial.println(IR1);
    //    Serial.print(", ");
    //    Serial.println(weight);
    if ((abs(cur_vl - pre_vl)) < 1 && vldist <= scaleVal + 10) {
      Serial.print("weight");
      Serial.println(weight);
      //      weight= round(weight);
      //      weight*=5;
      if(gydist>=(c_array[0]/2)&&gydist<c_array[0]+((c_array[1]-c_array[0])/2)) weight=1;
      for (int i = 1; i < c_cnt-1; i++) {
        if (gydist >= c_array[i]-((c_array[i]-c_array[i-1])/2) && gydist < c_array[i]+((c_array[i+1]-c_array[i])/2)) weight = i + 1;
      }
        if (gydist >= c_array[c_cnt-1]-((c_array[c_cnt-1]-c_array[c_cnt-2])/2)) weight = c_cnt;
    }

    if (cur_vl - pre_vl >= 50) {
      Serial.print("prev_vl ");
      Serial.println(pre_vl);
      Serial.print("cur_vl  ");
      Serial.println(weight);

      Serial.println(cur_vl);
      ircnt = true;
    }

    if (accelcnt) {
      accel_delay++;

      if (accel_delay > 2) {
        accel_delay = 0;
        accelcnt = false;
      }
      if (ircnt) {
        digitalWrite(15, HIGH);
        Serial.println("count!!");
        str = "$r" + String(int(weight)) + ";";

        if (deviceConnected) {
          pTxCharacteristic->setValue(str.c_str());
          pTxCharacteristic->notify();
          delay(10); // bluetooth stack will go into congestion, if too many packets are sent
        }
        ircnt = false;
        accelcnt = false;
        accel_delay = 0;
        ir_delay = 0;
        delay(150);
      }
    }
    if (ircnt) {
      ir_delay++;
      if (ir_delay > 1) {
        ir_delay = 0;
        ircnt = false;
      }
    }
  }



  else {
    if (rawd_stat) {

      IR_ME();
      Serial.println(gydist);
      str = "$d" + String(gydist) + ";";
      if (deviceConnected) {
        pTxCharacteristic->setValue(str.c_str());
        pTxCharacteristic->notify();
        delay(50); // bluetooth stack will go into congestion, if too many packets are sent
      }
    }

    else {
      if (reset_stat) {
        ACCEL_ME(str);

        if (accelcnt) {
          digitalWrite(15, HIGH);
          Serial.println("count!!");
          str = "$derr;";

          if (deviceConnected) {
            pTxCharacteristic->setValue(str.c_str());
            pTxCharacteristic->notify();
            delay(10); // bluetooth stack will go into congestion, if too many packets are sent
          }
          accelcnt = false;
          delay(150);
        }
      }
      if (c_stat) {
        Serial.print("go  ");
        Serial.print(c_array[c_cnt - 1]);
        Serial.print("cnt  ");
        Serial.print(c_cnt - 1);
        for (int i = 0; i < 10; i++) {
          IR_ME();
          c_array[c_cnt - 1] += gydist;
        }
        c_array[c_cnt - 1] /= 10;
        c_array[c_cnt - 1] = round(c_array[c_cnt - 1]);
        while (vldist == 201) {
          IR_ME();
        }
        IRVal_2 += vldist;
        str = "$me;";
        Serial.println("done");
        if (deviceConnected) {
          Serial.println("con");
          
          pTxCharacteristic->setValue(str.c_str());
          pTxCharacteristic->notify();
          delay(10); // bluetooth stack will go into congestion, if too many packets are sent

        }
        c_stat = false;
      }
    }
  }
  // disconnecting
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

  if (maxflag)maxCount++;
  else maxCount = 0;
  if (minflag)minCount++;
  else minCount = 0;

  //  accelcnt = false;
  //  ircnt = false;
}
//...........................................................................................................
void setID() {
  // all reset
  digitalWrite(SHT_LOX1, LOW);
  digitalWrite(SHT_LOX2, LOW);
  delay(10);
  // all unreset
  digitalWrite(SHT_LOX1, HIGH);
  digitalWrite(SHT_LOX2, HIGH);
  delay(10);

  // activating LOX1 and reseting LOX2
  digitalWrite(SHT_LOX1, HIGH);
  digitalWrite(SHT_LOX2, LOW);

  // initing LOX1
  if (!lox.begin(LOX1_ADDRESS)) {
    Serial.println(F("Failed to boot first VL53L0X"));
    pinMode(18, OUTPUT);
    while (1);
  }
  delay(10);

  // activating LOX2
  digitalWrite(SHT_LOX2, HIGH);
  delay(10);

  //initing LOX2
  if (!lox2.begin(LOX2_ADDRESS)) {
    Serial.println(F("Failed to boot second VL53L0X"));
    pinMode(18, OUTPUT);
    while (1);
  }
}

void BLE_SET() {
  // Create the BLE Device
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
}

void ACCEL_SET() {
  if (!accel.begin())
  {
    /* There was a problem detecting the ADXL345 ... check your connections */
    Serial.println("Ooops, no ADXL345 detected ... Check your wiring!");
    pinMode(18, OUTPUT);

    while (1);
  }
  /* Set the range to whatever is appropriate for your project */
  //accel.setRange(ADXL345_RANGE_16_G);
  accel.setRange(ADXL345_RANGE_8_G);
  // accel.setRange(ADXL345_RANGE_4_G);
  // accel.setRange(ADXL345_RANGE_2_G);

  /* Display some basic information on this sensor */
  displaySensorDetails();

  /* Display additional settings (outside the scope of sensor_t) */
  displayDataRate();
  displayRange();
  Serial.println("");
  sensors_event_t event;
  accel.getEvent(&event);
  for (int i = 0 ; i < 5 ; i ++)
  {
    maxBuff[i] = event.acceleration.z;
    minBuff[i] = event.acceleration.z;
    preMax = event.acceleration.z;
    preMin = event.acceleration.z;
  }
}

void ACCEL_ME(String str) {
  //
  //  Serial.print("cm");
  //  Serial.print(maxCount);
  //  Serial.print(" ");
  //  Serial.print("cM");
  //  Serial.print(minCount);
  //  Serial.println(" ");
  if (maxCount > 1)
  {
    maxCount = 0;
    maxflag = false;
  }

  if (minCount > 1)
  {
    minCount = 0;
    minflag = false;
  }
  for (int i = 0 ; i < 5 ; i ++)
  {
    maxBuff[i] = 0;
    minBuff[i] = 100;
  }
  for (int i = 0 ; i < 20 ; i ++)
  {
    sensors_event_t event;
    accel.getEvent(&event);
    for (int j = 0 ; j < 5 ; j ++)
    {
      if (maxBuff[j] < event.acceleration.z)
      {
        if (j < 4)
        {
          maxBuff[j + 1] = maxBuff[j];
          maxBuff[j] = event.acceleration.z;
        } else
        {
          maxBuff[j] = event.acceleration.z;
        }
      } else if (minBuff[j] > event.acceleration.z)
      {
        if (j < 4)
        {
          minBuff[j + 1] = maxBuff[j];
          minBuff[j] = event.acceleration.z;
        } else
        {
          minBuff[j] = event.acceleration.z;
        }
      }
    }
    delay(1);
  }
  currMax = 0;
  currMin = 0;
  for (int i = 0 ; i < 5 ; i ++)
  {
    currMax += maxBuff[i];
    currMin += minBuff[i];
  }

  currMax = currMax / 5;
  currMin = currMin / 5;
  if (currMin - preMin < -2.6)
  {
    minflag = true;
    minCount = 0;
  }


  str = "";
  if (currMax - preMax > 2.6)
  {
    //    Serial.print("1");
    //    Serial.print(maxflag);
    //    Serial.print(minflag);
    if (!maxflag && !minflag)
    {
      countFlag = true;
    }
    //    else if (maxCount == 1)
    //    {
    if (countFlag)
    {
      //            Serial.print(currMax);
      //            Serial.print(" ");
      //            Serial.print(currMin);
      //            Serial.println(" ");
      accelcnt = true;
      delay(10);
      countFlag = false;
    }
    //    }
    maxflag = true;
    maxCount = 0;
  } else
  {
    //    Serial.println("1");
    countFlag = false;
  }
  //  delay(100);
}
void IR_ME() {

  VL53L0X_RangingMeasurementData_t measure;
  VL53L0X_RangingMeasurementData_t measure2;

  //  Serial.print("Reading a measurement... ");
  lox.rangingTest(&measure, false); // pass in 'true' to get debug data printout!
  lox2.rangingTest(&measure2, false); // pass in 'true' to get debug data printout!

  if (measure2.RangeStatus != 4) {  // phase failures have incorrect data
    vldist = measure2.RangeMilliMeter / 10;
    //    Serial.print("VL dist : ");
    //    Serial.print(vldist);
    //    Serial.print("mm");
    //    Serial.print("    ");
  }

  else vldist = 201;
  //  else Serial.println(" out of range ");

  if (vldist > 250) vldist = 201;
  delay(100);

  if (measure.RangeStatus != 4) {  // phase failures have incorrect data
    gydist = measure.RangeMilliMeter;
    gydist /= 10;
  }

  else gydist = 0;
  //  else Serial.println(" out of range ");

  if (gydist > 250) gydist = 0;
  delay(100);
  //    Serial.print("VL dist : ");
  //        Serial.print(vldist);
  //        Serial.print("cm");
  //        Serial.print("    ");
  //    Serial.print("gy dist : ");
  //
  //    Serial.print(gydist);
  //
  //    Serial.println("CM");
}
