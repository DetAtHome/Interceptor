#include <Servo.h>
#include <BrightBricks.h>
#include <RemoteStepper.h>
#include <RemoteSwitch.h>

char string[32];
boolean fullyRead=false;
int i;
char read;
boolean probeState=false;
char buf[15];
Servo probeServo;
long highVal=0;
BrickBus bus(10);
RemoteStepper stepper(64);
RemoteSwitch rswitch(10);
bool hasConnectionFailure=false;

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

static void stepperReported(char* state) {

  Serial.println("ok");  
}

void setup() {
  Serial.begin(115200);
  pinMode(A0,INPUT);
  pinMode(A7,INPUT);
  pinMode(11,OUTPUT);
  pinMode(10,INPUT);
  pinMode(A3,OUTPUT);

  Serial.println("ExCtr 0.12"); 
  probeServo.attach(A3);  
  probeServo.write(120);
  pinMode(3,OUTPUT);
  digitalWrite(3,HIGH);
  delay(500);
  digitalWrite(3,LOW);
  bus.initialize(13,errorCallback);
  rswitch.initialize(&bus);
  stepper.initialize(&bus);
  /*
  stepper.setStepsPerRevolution(200);
  stepper.setReportFunction(stepperReported);
  stepper.setMaxSpeed(200);
  stepper.setReportThreshold(0);
  stepper.setAcceleration(50);
*/
  Serial.println("ok");
}

void loop() {
    bus.update();
  // put your main code here, to run repeatedly:
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
  if (fullyRead) { 
  
    i=0;
    char command = string[0];
    char* ptrParam = & string[1];
    long param = atol(ptrParam);
    switch (command) {
      case '?':
        if (param==1) {
          // probe state queried
          Serial.print("probe");
          if (probeState) {
            Serial.println("ON");
          } else {
            Serial.println("OFF");
          }
        }
        break;
      case 'p':
        if(param==0) {
          probeServo.write(120);
          probeState = false;
         //Serial.println("Probe Off");
        } else {
          probeServo.write(100);
          probeState = true;
          Serial.println("Probe On");
        }
        break;
      case 's':
        analogWrite(11, map(param,0,1000,0,255));
        
//        analogWrite(A1, param);
        break;

      case 't':
        for (int j=0;j<param;j++) {
          for(int i=0;i<255;i++) {
            Serial.println(i);
            analogWrite(11, i);
            delay(20);
          }
          for(int i=255;i>0;i--) {
            Serial.println(i);
            analogWrite(11, i);
            delay(20);
          }
        }        
        break;
      case 'i':
//        Serial.println("init");
        stepper.setStepsPerRevolution(200);
        stepper.setMaxSpeed(200);
        stepper.setReportThreshold(0);
        stepper.setAcceleration(50);
        break;
      case 'o':
        // load, param defines slot
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
      case 'l':
      // load, param defines slot
         stepper.move(1500);
         break;
      case 'r':
      // unload
         stepper.move(-1500);
         break;       
      case 'h':

        if(digitalRead(10)==HIGH) {
          Serial.println("h0020");
        } else {
          Serial.println("h0001");
        } 
        break;

    }

    fullyRead=false;
    for(int j=0;j<32;j++) { string[j]= 0; }
  }
//  analogWrite(11, map(in,0,1023,0,255));

}
