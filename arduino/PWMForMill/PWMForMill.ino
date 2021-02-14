#include <Servo.h>
char string[32];
boolean fullyRead=false;
int i;
char read;
boolean probeState=false;

Servo probeServo;

void setup() {
  Serial.begin(115200);
  pinMode(A0,INPUT);
  pinMode(11,OUTPUT);
  pinMode(A3,OUTPUT);
  probeServo.attach(A3);  
  probeServo.write(120);
  pinMode(3,OUTPUT);
  digitalWrite(3,HIGH);
  delay(500);
  digitalWrite(3,LOW);
}

void loop() {
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
    Serial.print("Read> ");
    Serial.println(string);
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
      
    }
    
    fullyRead=false;
    for(int j=0;j<32;j++) { string[j]= 0; }
  }
//  analogWrite(11, map(in,0,1023,0,255));

}
