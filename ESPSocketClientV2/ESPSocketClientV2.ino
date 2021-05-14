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
bool sendToBrightBricks = false;
String brightBricksCommand = "";

// ------ static BrightBricks callbacks
static void errorCallback(byte device, int code) {
  if (code==404) {
    if (!hasConnectionFailure) {
      outgoingClient.println("#Connection failure, this is hardly recoverable, fix it");
      outgoingClient.flush();
      hasConnectionFailure=true;
    }
  } else {
    outgoingClient.print("#ERROR: From ");
    outgoingClient.print(device);
    outgoingClient.print(" Code: ");
    outgoingClient.println(code);
    outgoingClient.flush();
  }
}

static void stepperReported(char* state) {
  
  outgoingClient.print("#ReportFunction called with: ");
  outgoingClient.print(state);  
  long stepReported = atol(state);
  outgoingClient.print(" / ");
  outgoingClient.println(stepReported); 
  outgoingClient.flush();
}
// -------------------------------------------------


// ---- business logic ----------
bool skipNextCR = false;

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
  while(!outgoingClient.connected()) {outgoingClient.connect(host, 9023);}

}

void loop() {

  // read data from cnc shield
  if(Serial.available()>0) {
     outgoingClient.write(Serial.read());
  } 
  if(outgoingClient.available()>0) {
    char c = outgoingClient.read();
    if (c=='#') {
//       sendToBrightBricks=true;
    }
    if (c==13 && sendToBrightBricks) {
      computeBrightBricksCommand(brightBricksCommand);
      brightBricksCommand="";
      sendToBrightBricks=false;
    }
    if(sendToBrightBricks) {
      brightBricksCommand = brightBricksCommand + c;    
    } else {
      Serial.write(c);
    }
  }
  if(!outgoingClient.connected()) {
    // try to reconnect
    outgoingClient.connect(host, 9023);
  }
  
/*
while (outgoingClient.available()) {
  Serial.write(outgoingClient.read());
}

//check UART for data
  if (Serial.available()) {
    size_t len = Serial.available();
    uint8_t sbuf[len];
    Serial.readBytes(sbuf, len);
    //push UART data to all connected telnet clients
    if (outgoingClient && outgoingClient.connected()) {
      outgoingClient.write(sbuf, len);
    }
  }
*/  
}

void computeBrightBricksCommand(String command) {
  outgoingClient.println(command);
  outgoingClient.flush();
}
