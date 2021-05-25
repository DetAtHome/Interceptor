#include <SoftwareSerial.h>
#include <BrightBricks.h>
#include <RemoteGenericPWM.h>

SoftwareSerial controller(7,8);
BrickBus bus(10);
RemoteGenericPWM rgeneric(15);


bool hasConnectionFailure=false;
char string[32];
boolean fullyRead=false;
int i;
char read;
long lastPing = millis();

static void errorCallback(byte device, int code) {
  if (code==404) {
    if (!hasConnectionFailure) {
      Serial.println("Connection failure, this is hardly recoverable, fix it");
      hasConnectionFailure=true;
    }
  } else {
    Serial.print("ERROR: From ");
    Serial.print(device);
    Serial.print(" Code: ");
    Serial.println(code);
  }
}
void setup() {
  Serial.begin(115200);
  pinMode(9,OUTPUT);
  controller.begin(9600);
  bus.initialize(11,errorCallback);
  rgeneric.initialize(&bus);
  
}

void loop() {
//  if(controller.available()){
//    Serial.write(controller.read());
//  }

  bus.update();
  if (Serial.available() > 0) {
     read = Serial.read();
     if (read==';') { 
       fullyRead=true; 
       Serial.flush();
     } else {
       string[i] = read;
       i++;
     }
     if(i>31) i=0;
  }
  if (controller.available() > 0) {
     read = controller.read();
     Serial.write(read);
     if (read=='\r') { 
       fullyRead=true; 
       controller.flush();
     } else {
       string[i] = read;
       i++;
     }
     if(i>31) i=0;
  }
  if (fullyRead) {
    i=0;
    char command;
    char* ptrParam;
    
    if (string[0]=='#') {
      command = string[1];
      ptrParam = & string[2];
    } else {
      command = string[0];
      ptrParam = & string[1];
    }
    long param = atol(ptrParam);
    Serial.println(command);
    Serial.println(param);
    fullyRead=false;
    switch(command) {
    case 'd':
      if(rgeneric.setControlledPin(param)) {
        Serial.println("#ok");
        controller.print("#ok\r");
        controller.flush();
      } else {
        Serial.println("#buserror");
        controller.print("#buserror\r");
        controller.flush();
      }
      break;
    case 's':
      int i = map(param,0,20000,0,255);
      analogWrite(9,i);
      if(rgeneric.setPWM(i)) {
        Serial.println("#ok");
        controller.print("#ok\r");
        controller.flush();
      } else {
        Serial.println("#buserror");
        controller.print("#buserror\r");
        controller.flush();
      }
      break;
        
    }
    for(int j=0;j<32;j++) { string[j]= 0; }


  }
//  if(millis()-lastPing>1000) {
//      Serial.println("Ping");
//      controller.print("#ping\r");
//      controller.flush();
//      lastPing=millis();
//    }
}
