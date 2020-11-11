#include <Arduino.h>
#include <bluefruit.h>

// 113A0001-FD33-441B-9A57-E9F1C29633D3 => service
// 113A0002-FD33-441B-9A57-E9F1C29633D3 => characteristic
// https://yupana-engineering.com/online-uuid-to-c-array-converter

uint8_t const mmDispenserSerivceUuid[] = {0xD3, 0x33, 0x96, 0xC2, 0xF1, 0xE9, 0x57, 0x9A, 0x1B, 0x44, 0x33, 0xFD, 0x01, 0x00, 0x3A, 0x11};
uint8_t const mmDispenserStateCharacteristicUuid[] = {0xD3, 0x33, 0x96, 0xC2, 0xF1, 0xE9, 0x57, 0x9A, 0x1B, 0x44, 0x33, 0xFD, 0x02, 0x00, 0x3A, 0x11};
uint8_t const mmDispenserDispenseCharacteristicUuid[] = {0xD3, 0x33, 0x96, 0xC2, 0xF1, 0xE9, 0x57, 0x9A, 0x1B, 0x44, 0x33, 0xFD, 0x03, 0x00, 0x3A, 0x11};

BLEService mmDispenserService = BLEService(mmDispenserSerivceUuid);

BLECharacteristic mmDispenserStateCharacteristic = BLECharacteristic(mmDispenserStateCharacteristicUuid);
BLECharacteristic mmDispenserDispenseCharacteristic = BLECharacteristic(mmDispenserDispenseCharacteristicUuid);

bool dispenserState = false;

void connectedCallback(uint16_t connectionHandle)
{
  char centralName[32] = {0};
  BLEConnection *connection = Bluefruit.Connection(connectionHandle);
  connection->getPeerName(centralName, sizeof(centralName));
  Serial.print(connectionHandle);
  Serial.print(", connected to ");
  Serial.print(centralName);
  Serial.println();
}

void disconnectedCallback(uint16_t connectionHandle, uint8_t reason)
{
  Serial.print(connectionHandle);
  Serial.print(" disconnected, reason = ");
  Serial.println(reason); // see https://github.com/adafruit/Adafruit_nRF52_Arduino
  // /blob/master/cores/nRF5/nordic/softdevice/s140_nrf52_6.1.1_API/include/ble_hci.h
  Serial.println("Advertising ...");
}

void cccdCallback(uint16_t connectionHandle, BLECharacteristic *characteristic, uint16_t cccdValue)
{
  if (characteristic->uuid == mmDispenserStateCharacteristic.uuid)
  {
    Serial.print("Dispenser State 'Notify', ");
    if (characteristic->notifyEnabled())
    {
      Serial.println("enabled");
    }
    else
    {
      Serial.println("disabled");
    }
  }
}

void writeCallback(uint16_t connectionHandle, BLECharacteristic *characteristic, uint8_t *data, uint16_t len)
{
  if (characteristic->uuid == mmDispenserDispenseCharacteristic.uuid)
  {
    Serial.print("Dispense ");
    dispenserState = data[0] != 0x00;
    Serial.println(dispenserState ? "enabled" : "disabled");
  }
}

void setupDispenserService()
{
  mmDispenserService.begin();

  mmDispenserStateCharacteristic.setProperties(CHR_PROPS_READ | CHR_PROPS_NOTIFY);
  mmDispenserStateCharacteristic.setPermission(SECMODE_OPEN, SECMODE_NO_ACCESS);
  mmDispenserStateCharacteristic.setFixedLen(2);
  mmDispenserStateCharacteristic.setCccdWriteCallback(cccdCallback); // Optionally capture CCCD updates
  mmDispenserStateCharacteristic.begin();

  mmDispenserDispenseCharacteristic.setProperties(CHR_PROPS_READ | CHR_PROPS_WRITE | CHR_PROPS_WRITE_WO_RESP);
  mmDispenserDispenseCharacteristic.setPermission(SECMODE_NO_ACCESS, SECMODE_OPEN);
  mmDispenserDispenseCharacteristic.setFixedLen(1);
  mmDispenserDispenseCharacteristic.setWriteCallback(writeCallback, true);
  mmDispenserDispenseCharacteristic.begin();
}

void startAdvertising()
{
  Bluefruit.Advertising.addFlags(BLE_GAP_ADV_FLAGS_LE_ONLY_GENERAL_DISC_MODE);
  Bluefruit.Advertising.addTxPower();
  Bluefruit.Advertising.addService(mmDispenserService);
  Bluefruit.Advertising.addName();

  // See https://developer.apple.com/library/content/qa/qa1931/_index.html
  const int fastModeInterval = 32;  // * 0.625 ms = 20 ms
  const int slowModeInterval = 244; // * 0.625 ms = 152.5 ms
  const int fastModeTimeout = 30;   // s
  Bluefruit.Advertising.restartOnDisconnect(true);
  Bluefruit.Advertising.setInterval(fastModeInterval, slowModeInterval);
  Bluefruit.Advertising.setFastTimeout(fastModeTimeout);
  // 0 = continue advertising after fast mode, until connected
  Bluefruit.Advertising.start(0);
  Serial.println("Advertising ...");
}

void setup()
{
  Serial.begin(115200);
  while (!Serial)
  {
    delay(10);
  } // only if usb connected
  Serial.println("Setup");

  Bluefruit.begin();
  Bluefruit.setName("nRF52840");
  Bluefruit.Periph.setConnectCallback(connectedCallback);
  Bluefruit.Periph.setDisconnectCallback(disconnectedCallback);

  setupDispenserService();
  startAdvertising();
}

void loop()
{
  if (Bluefruit.connected()) {
    
    int dispenserStateInt = dispenserState ? 1 : 0;
    uint8_t dispenserStateHiByte = (uint8_t) (dispenserStateInt >> 8);
    uint8_t dispenserStateLoByte = (uint8_t) dispenserStateInt;
    uint8_t dispenserStateData[2] = { dispenserStateHiByte, dispenserStateLoByte };
    if (mmDispenserStateCharacteristic.notify(dispenserStateData, sizeof(dispenserStateData))) {
      Serial.print("Notified, dispenser state = ");
      Serial.println(dispenserStateInt);
    } else {
      Serial.println("Notify not set, or not connected");
    }
  }
  delay(1000); // ms
}