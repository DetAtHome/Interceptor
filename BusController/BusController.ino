#include <SoftwareSerial.h>
#include <BrightBricks.h>
#include <RemoteGenericPWM.h>
#define LOG_DEBUG true

// was 7 to test orig was 12
#define BRICKBUS_PIN 7
//was 11
#define SPINDLE_PIN 6
#define SERIAL_RX 10
#define SERIAL_TX 9

SoftwareSerial controller(SERIAL_RX, SERIAL_TX);
BrickBus bus(10);
RemoteGenericPWM rgeneric(15);


bool hasConnectionFailure=false;
char string[32];
boolean fullyRead=false;
int i;
char read;
long lastPing = millis();
boolean doPing = true;

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
  Serial.println("Start");
  pinMode(SPINDLE_PIN,OUTPUT);
  Serial.println("pinmode");
  controller.begin(9600);
  bus.initialize(BRICKBUS_PIN, errorCallback);
  rgeneric.initialize(&bus);
  Serial.println("Inited");
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
    case 't':
     case 'T':
      analogWrite(SPINDLE_PIN, param);
      Serial.println(param);
      break;
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
    case 'g':
      if(rgeneric.setPWM(param)) {
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
    case 'S':
      int i = map(param,0,20000,0,255);
      analogWrite(SPINDLE_PIN, i);
      controller.print("#ok\r");
      controller.flush();
      break;
    
    }
    for(int j=0;j<32;j++) { string[j]= 0; }


  }
  if(doPing) {
      Serial.println("Ping");
      controller.print("#ping\r");
      controller.flush();
      doPing = false;
    }
}
