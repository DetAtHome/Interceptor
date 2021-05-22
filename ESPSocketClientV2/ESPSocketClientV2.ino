#include <ESP8266WiFi.h>
#include "defines.h"
#include <BrightBricks.h>
#include <RemoteStepper.h>
#include <RemoteSwitch.h>
#include <RemoteGenericPWM.h>

  
// --------- WIFI related -------------------
WiFiClient outgoingClient;


// ---------- BrightBricks related ----------
bool hasConnectionFailure=false;
BrickBus bus(10);
RemoteSwitch rswitch(10);
RemoteGenericPWM rgeneric(15);

// ------ Business logic --------------------
const char CR = 13;
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

static void pwmStateReported(char* state) {
  debug("Aaaa",0);
}

void setup()
{

  // cnc shield comm
  Serial.begin(115200);
  
  Serial.println("\r\nGo");
  
  // wifi setup
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
  }
  outgoingClient.setNoDelay(true);
  outgoingClient.connect(host, 9023);
  while(!outgoingClient.connected()) {outgoingClient.connect(host, 9023);}
  outgoingClient.println("#connected");
  outgoingClient.flush();
  delay(500);
  // BrightBricks setup
  bus.initialize(5,errorCallback);
  debug("Bus initialized",0);
  while(!rgeneric.setControlledPin(1)) {
    delay(10);
    bus.update();
    debug(".",0);
  }
  debug("Spindle initialized",0);

}

void loop() {

    bus.update();
    // read data from cnc shield
    if(Serial.available()>0) {
       outgoingClient.write(Serial.read());
    } 
    if(outgoingClient.available()>0) {
      char c = outgoingClient.read();
      if (c=='#') {
         sendToBrightBricks=true;
      }
  
      if(sendToBrightBricks) {
        brightBricksCommand = brightBricksCommand + c;    
      } else {
        Serial.write(c);
      }
      if (c==13 && sendToBrightBricks) {
        computeBrightBricksCommand(brightBricksCommand);
        brightBricksCommand="";
        sendToBrightBricks=false;
      }
    }
}

void computeBrightBricksCommand(String command) {
  char cmd = command.substring(1,2).charAt(0);
  int param = command.substring(2).toInt();
  switch(cmd) {
  case 'O': 
    switch (param) {
      case 1:
        rswitch.enable5V();
        break;
      case 2:
        rswitch.enableVdd();
        break;
      case 3:
        rswitch.disable5V();
        break;
      case 4:
       rswitch.disableVdd();
        break;
    }
    break;
  case 'S': 
    if (rgeneric.setPWM((int)param)) {
      outgoingClient.print("#ok\r");
      outgoingClient.flush();
    } else {
      outgoingClient.print("#buserror\r");
      outgoingClient.flush();
      
    }
    break;

  }
  
  debug("done",command.substring(2).toInt());
  
}

void debug(String text, int val) {

  char promptBuffer[200];
  text.toCharArray(promptBuffer, text.length()+1);
  char buffer[2000];
  sprintf(buffer, "#%s %d\r",promptBuffer, val);
  outgoingClient.print(buffer);
  outgoingClient.flush();
 }
