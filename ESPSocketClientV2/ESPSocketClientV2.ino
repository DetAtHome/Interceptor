#include <SoftwareSerial.h>

#include <ESP8266WiFi.h>
#include "defines.h"

  
// --------- WIFI related -------------------
WiFiClient outgoingClient;
WiFiClient extraClient;

// ------ Business logic --------------------
SoftwareSerial controller(D7,D8);
long lastHeartbeatReceived=0;
long lastHeartbeatSent=0;

void setup()
{

  
  Serial.begin(115200);
  controller.begin(9600);
  
  // wifi setup
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
  }
  setupConnections();
  debug("#initialized",0);

}

void loop() {

    // read data from cnc shield
    if(Serial.available()>0) {
      outgoingClient.write(Serial.read());
//       if (outgoingClient.write(Serial.read()>0)) {
//         lastHeartbeatSent=millis();
//       }
    } 
    if(controller.available()) {
      extraClient.write(controller.read());
    }
    
    if(outgoingClient.available()>0) {
      char c = outgoingClient.read();
 /*     if(c==94) {
        lastHeartbeatReceived=millis();
      } else { */
        Serial.write(c);
//      }
    }
    
    if(extraClient.available()>0) {
      char c = extraClient.read();
      controller.write(c);
    }
/*
    // No incoming heartbeat for a second
    if((millis()-lastHeartbeatReceived)>1200) {
      setupConnections();
      lastHeartbeatReceived = millis();
    }

    // try to write a heartbeat and renew if needed
    if((millis()-lastHeartbeatSent)>1000) {
      if (outgoingClient.write(94)<0) {
        setupConnections();
      }
      lastHeartbeatSent=millis();
    }
    */
}


void debug(String text, int val) {

  char promptBuffer[200];
  text.toCharArray(promptBuffer, text.length()+1);
  char buffer[2000];
  sprintf(buffer, "#%s %d\r",promptBuffer, val);
  extraClient.print(buffer);
  extraClient.flush();
 }

 void setupConnections() {
  outgoingClient.setNoDelay(true);
  outgoingClient.connect(host, 9023);
  while(!outgoingClient.connected()) {outgoingClient.connect(host, 9023);}
  
  extraClient.setNoDelay(true);
  extraClient.connect(host, 9024);
  while(!extraClient.connected()) {extraClient.connect(host, 9024);}
  extraClient.println("#connected both");
  extraClient.flush();

 }
