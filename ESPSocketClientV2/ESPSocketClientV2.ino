#include <SoftwareSerial.h>

#include <ESP8266WiFi.h>
#include "defines.h"

  
// --------- WIFI related -------------------
WiFiClient outgoingClient;
WiFiClient extraClient;
WiFiClient heartbeatClient;

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
    } 
    if(controller.available()) {
      extraClient.write(controller.read());
    }
    
    if(outgoingClient.available()>0) {
      char c = outgoingClient.read();
      Serial.write(c);
    }
    
    if(extraClient.available()>0) {
      char c = extraClient.read();
      controller.write(c);
    }

    if(heartbeatClient.available()>0) {
      if(heartbeatClient.read()<0) {
        // stale connection
        setupConnections();
      } else {
        lastHeartbeatReceived = millis();
      }
    }

    // No incoming heartbeat for a second
    if((millis()-lastHeartbeatReceived)>1200) {
      setupConnections();
      lastHeartbeatReceived = millis();
    }

    // try to write a heartbeat and renew if needed
    if((millis()-lastHeartbeatSent)>1000) {
      int wrte = heartbeatClient.write(94);
      if (wrte < 0) {
        setupConnections();
      } else {
        lastHeartbeatSent=millis();
      }
    }
    
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
