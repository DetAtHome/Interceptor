#include <ESP8266WiFi.h>
#include "defines.h"
#include <BrightBricks.h>
#include <RemoteStepper.h>
#include <RemoteSwitch.h>
  
// --------- WIFI related -------------------
WiFiClient outgoingClient;


// ---------- BrightBricks related ----------
bool hasConnectionFailure=false;
BrickBus bus(10);
RemoteStepper stepper(61);

// ------ Business logic --------------------
char* commandBuffer;
bool doSendToSerial = false;
bool doSendToSocket = false;
String cncData;
String socketData;

// ------ static BrightBricks callbacks
static void errorCallback(byte device, int code) {
  if (code==404) {
    if (!hasConnectionFailure) {
      outgoingClient.println(">Connection failure, this is hardly recoverable, fix it");
      hasConnectionFailure=true;
    }
  } else {
    outgoingClient.print(">ERROR: From ");
    outgoingClient.print(device);
    outgoingClient.print(" Code: ");
    outgoingClient.println(code);
  }
}

static void stepperReported(char* state) {
  
  outgoingClient.print(">ReportFunction called with: ");
  outgoingClient.print(state);  
  long stepReported = atol(state);
  outgoingClient.print(" / ");
  outgoingClient.println(stepReported); 
  
}
// -------------------------------------------------


// ---- business logic ----------
void setup()
{

  // cnc shield comm
  Serial.begin(115200);
  // wifi setup
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
  }
  
  outgoingClient.setNoDelay(true);
  outgoingClient.connect(host, 9023);
  // BrightBricks setup
  bus.initialize(13,errorCallback);
  commandBuffer = (char*)malloc(1024);
  
}
void loop() {

  // read data from cnc shield
  if(Serial.available()>0) {
    cncData = Serial.readStringUntil('\n');
    doSendToSocket = true;
  }

  // send out that data via socket to pc
  if(doSendToSocket) {
      if (outgoingClient.connected()||outgoingClient.available() ) {
        outgoingClient.print(cncData);
        doSendToSocket=false;
      }
  }

  // read incoming from socket
  if (outgoingClient.available()>0) {
    socketData = outgoingClient.readStringUntil('\n');
    doSendToSerial = true;
//    char c = outgoingClient.read();
//    Serial.print(c);
  }

  // if socketData starts with '>' that data goes to brightbus
  if(socketData.charAt(0)=='>') {
    outgoingClient.println(">BrightBus command detected");
    socketData="";
    doSendToSerial = false;
  }
  if(doSendToSerial) {
    Serial.print(socketData);
    doSendToSerial = false;
  }

  if(!outgoingClient.connected()) {
    // try to reconnect
    outgoingClient.connect(host, 9023);
  }

}
