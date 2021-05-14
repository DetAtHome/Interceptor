#include <ESP8266WiFi.h>
#include "defines.h"

  
WiFiClient outgoingClient;
WiFiClient incomingClient;

WiFiServer wifiServer(6666);

boolean isConnected = false;
String serialData;
String requestFromPC="";
bool requestFromPCPending = false;
bool doSend = false;

void setup()
{
  Serial.begin(115200);
  Serial.println();

  Serial.printf("Connecting to %s ", ssid);
  WiFi.begin(ssid, password);
  wifiServer.begin();
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }
  
  outgoingClient.setNoDelay(true);
  outgoingClient.connect(host, 9023);
  
  Serial.println("Inited");  
}
void loop() {
  if(Serial.available()>0) {
    serialData = Serial.readStringUntil('\n');
    doSend = true;
  }
  if(doSend) {
      if (outgoingClient.connected()||outgoingClient.available() ) {
        outgoingClient.println(serialData);
        doSend=false;
      }
  }
  if (outgoingClient.available()>0) {
    char c = outgoingClient.read();
    Serial.print(c);
  }

}
/*
void loop() {
  
  // server part: react to incoming connection data
  WiFiClient incomingClient = wifiServer.available();
  if (incomingClient) {
 
    while (incomingClient.connected()) {
      while (incomingClient.available()>0) {
        requestFromPC = incomingClient.readStringUntil('\n');
        Serial.println(requestFromPC);
        requestFromPCPending = true;
      }
 
      delay(10);
    }
  }

  // There might be some answer from Serial
  if(Serial.available()>0) {
    serialData = Serial.readStringUntil('\n');
    doSend = true;
  }
  
  if (doSend) {
    
    if (requestFromPCPending) {
      // answer that request with whatever the mill told me      
      incomingClient.print(serialData + "\r\n");
      incomingClient.flush();
      requestFromPCPending = false;
      doSend=false;
      // this incoming client connection has done its work
   //   incomingClient.stop();
    } else {
      // connect to PC Socketserver cause I have something to tell
      if (outgoingClient.connect(host, 9023)) {
        outgoingClient.print(serialData); 
        outgoingClient.flush();       
      }
    }
    // deal with the answer of the PC to my request (Intercepter should always answer OK, so no big deal)
    while (outgoingClient.connected() || outgoingClient.available())
    {
      if (outgoingClient.available())
      {
        String line = outgoingClient.readStringUntil('\n');
        // some debug data, normally I'd say I can drop that on the floor
        Serial.println(line);
        // ok so I have my answer, this client has done its work
  //      outgoingClient.stop();
        doSend=false;
      }
    }
  }
}
*/
