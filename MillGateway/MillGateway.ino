#include <ESP8266WiFi.h>
#include "defines.h"

#include <SoftwareSerial.h>

#define SOFTWARESERIAL_RX_PIN 12
#define SOFTWARESERIAL_TX_PIN 14

// --------- WIFI related -------------------
WiFiClient outgoingClient;
WiFiClient extraClient;
WiFiClient heartbeatClient;

SoftwareSerial softSer(SOFTWARESERIAL_RX_PIN,SOFTWARESERIAL_TX_PIN);
  

// ------ Business logic --------------------
char extraDataBuffer[1024];
int extraDataIndex=0;
long lastHeartbeatReceived=0;
long lastHeartbeatSent=0;
bool hasConnectionFailure=false;


void setup()
{

  softSer.begin(9600);
  
//  Serial.begin(115200);
  Serial.begin(19200);
 
  // wifi setup
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
  }
  setupConnections(0);

}

void loop() {
    // read data from cnc shield
//    if(softSer.available()>0) {
//      outgoingClient.write(softSer.read());
//    } 
//    
//    if(outgoingClient.available()>0) {
//      char c = outgoingClient.read();
//      softSer.write(c);
//    }
    if(Serial.available()>0) {
      outgoingClient.write(Serial.read());
    } 
    
    if(outgoingClient.available()>0) {
      char c = outgoingClient.read();
      Serial.write(c);
    }

//    if(Serial.available()>0) {
//      extraClient.write(Serial.read());
//    }  
//    
//    if(extraClient.available()>0) {
//      char c = extraClient.read();
//      Serial.write(c);
//    }
   if(softSer.available()>0) {
     extraClient.write(softSer.read());
   }  
    
    if(extraClient.available()>0) {
      char c = extraClient.read();
      softSer.write(c);
    }


/*
    if(Serial.available()>0) {
      char c = Serial.read();
      extraDataBuffer[extraDataIndex]=c;
      extraDataIndex++;
      if (c=='\r') {
        if(extraDataBuffer[0]=='#') {
           computeExtraData();
        } else {
          for (int i=0;i<extraDataIndex;i++) softSer.write(extraDataBuffer[i]);
        }
        extraDataIndex=0;
      }
    }

        
    if(extraClient.available()>0) {
      char c = extraClient.read();
      extraDataBuffer[extraDataIndex]=c;
      extraDataIndex++;
      if (c=='\r') {
        computeExtraData();
        extraDataIndex=0;
      }
    }
*/
    if(heartbeatClient.available()>0) {
      if(heartbeatClient.read()<0) {
        // stale connection
        setupConnections(1);
      } else {
        lastHeartbeatReceived = millis();
      }
    }

    // No incoming heartbeat for a second
    if((millis()-lastHeartbeatReceived)>2400) {
      setupConnections(2);
      lastHeartbeatReceived = millis();
    }

    // try to write a heartbeat and renew if needed
    if((millis()-lastHeartbeatSent)>1000) {
      int wrte = heartbeatClient.write(94);
      if (wrte < 0) {
        setupConnections(3);
      } else {
        lastHeartbeatSent=millis();
      }
    }
    
}

/*
void computeExtraData() {
    char command;
    char* ptrParam;
    
    if (extraDataBuffer[0]=='#') {
      command = extraDataBuffer[1];
      ptrParam = & extraDataBuffer[2];
    } else {
      command = extraDataBuffer[0];
      ptrParam = & extraDataBuffer[1];
    }
    long param = atol(ptrParam);
    Serial.println(command);
    Serial.println(param);
    switch(command) {
    case 't':
     case 'T':
      analogWrite(SPINDLE_PIN, param);
      Serial.println(param);
      break;
    case 'd':
      if(rgeneric.setControlledPin(param)) {
        Serial.println("#ok");
        extraClient.print("#ok\r");
      } else {
        Serial.println("#buserror");
        extraClient.print("#buserror\r");
      }
      break;
    case 'g':
      if(rgeneric.setPWM(param)) {
        Serial.println("#ok");
        extraClient.print("#ok\r");
      } else {
        Serial.println("#buserror");
        extraClient.print("#buserror\r");
      }
      break; 
    case 's':
    case 'S':
      int i = map(param,0,20000,0,1023);
      analogWrite(SPINDLE_PIN, i);
      extraClient.print("#ok\r");
      break;
    
    }
    for(int j=0;j<1024;j++) { extraDataBuffer[j]= 0; }


}

void debug(String text, int val) {

  char promptBuffer[200];
  text.toCharArray(promptBuffer, text.length()+1);
  char buffer[2000];
  sprintf(buffer, "#%s %d\r",promptBuffer, val);
  extraClient.print(buffer);
  extraClient.flush();
 }
*/

 void setupConnections(int from) {
  heartbeatClient.setNoDelay(true);
  heartbeatClient.connect(host, 9022);
  while(!heartbeatClient.connected()) {heartbeatClient.connect(host, 9022);}

  outgoingClient.setNoDelay(true);
  outgoingClient.connect(host, 9023);
  while(!outgoingClient.connected()) {outgoingClient.connect(host, 9023);}
  
  extraClient.setNoDelay(true);
  extraClient.connect(host, 9024);
  while(!extraClient.connected()) {extraClient.connect(host, 9024);}

  extraClient.println("#connected all");
  extraClient.flush();

 }

 
