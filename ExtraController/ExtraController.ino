#include <SoftwareSerial.h>
#define BRICKBUS_PIN 11
#define SPINDLE_PIN 6
#define SERIAL_RX 10
#define SERIAL_TX 9
#define LOG_DEBUG true
#define PJON_PACKET_MAX_LENGTH 15
#include <PJON.h>

SoftwareSerial controller(SERIAL_RX, SERIAL_TX);
PJON<SoftwareBitBang> pjonBus(1);

bool hasConnectionFailure=false;
char string[32];
boolean fullyRead=false;
int i;
char read;
long lastPing = millis();
boolean doPing = true;

void receiver_function(uint8_t *payload, uint16_t length, const PJON_Packet_Info &packet_info) {
  char buffer[10];
  memset(buffer,0,10);
  memcpy(buffer,payload,length);
  Serial.print("#");
  Serial.print(packet_info.sender_id);
  Serial.print(",");
  Serial.print(buffer);
  Serial.println();
//  controller.print("#");
//  controller.print(packet_info.sender_id);
//  controller.print(",");
//  controller.print(buffer);
//  controller.println();
};

void error_handler(uint8_t code, uint16_t data, void *custom_pointer) {
  #ifndef SILENT_ERROR
    if(code == PJON_CONNECTION_LOST) {
      Serial.print("#Connection with device ID ");
      Serial.print(pjonBus.packets[data].content[0], DEC);
      Serial.println(" is lost.");
      return;
    }
    if(code == PJON_PACKETS_BUFFER_FULL) {
      Serial.print("#Packet buffer is full, has now a length of ");
      Serial.println(data);
      Serial.println("Possible wrong bus configuration!");
      Serial.println("higher PJON_MAX_PACKETS if necessary.");
      return;
    }
    if(code == PJON_CONTENT_TOO_LONG) {
      Serial.print("#Content is too long, length: ");
      Serial.println(data);
      return;
    }
    Serial.print("#Error function called with unknown error: ");
    Serial.println(data);
  #endif
}


void setup() {
  Serial.begin(115200);
  Serial.println("#Start");
//  pjonBus.strategy.set_pin(BRICKBUS_PIN);
  pjonBus.strategy.set_pin(13);
  pjonBus.set_receiver(receiver_function);
  pjonBus.set_error(error_handler);

  pjonBus.begin();
//  controller.begin(9600);
//  controller.println("#Start");
  
  pinMode(SPINDLE_PIN,OUTPUT);
//  controller.println("#Inited ExtraController");
  Serial.println("#Inited ExtraController");

}

void loop() {
    pjonBus.update();
    pjonBus.receive();
    
//    if (Serial.available() > 0) {
//
//     read = Serial.read();
//     if (read==';') { 
//       fullyRead=true; 
//       Serial.flush();
//       Serial.print(';');
//       Serial.println();
//     } else if(read==13) {
//        // endemarkierung ohne semi> ich ignoriere das ersma. 
//     } else {
//       string[i] = read;
//       Serial.print(string[i]);
//       i++;
//     }
//
//     if(i>31) i=0;
//  }
  if (controller.available() > 0) {

     read = controller.read();
     if (read==';') { 
       fullyRead=true; 
       controller.flush();
//       Serial.print(';');
//       Serial.println();
     } else if(read==13) {
        // endemarkierung ohne semi> ich ignoriere das ersma. 
     } else if(read==10) {
        // endemarkierung ohne semi> ich ignoriere das ersma. 
     } else {
       string[i] = read;
//       Serial.print(string[i]);
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
    } else if (string[1]=='#') {
      command = string[2];
      ptrParam = & string[3];
    } else {
      command = string[0];
      ptrParam = & string[1];
    }

//    controller.print("#String content: ");
//    controller.println(string);
//
//   
//    controller.print("#parsed content: ");
//    controller.println(ptrParam);
//
//    controller.println(command);
    fullyRead=false;
    if(command=='-') {
      // minus sign indicates that this is a pjon bus string that is to be parsed
      // split into three parts by comma sign
      char *deviceIdAsString;
      deviceIdAsString = strtok(ptrParam, ",");
      char *commandIdAsString;
      commandIdAsString = strtok(NULL, ",");
      char *paramAsString;
      paramAsString = strtok(NULL, ",");
      int deviceId = atoi(deviceIdAsString);
      int commandId = atoi(commandIdAsString);
      long pjonParam = atoi(paramAsString);
      if (sendCommand(deviceId,commandId,pjonParam)) {
        controller.println("#pjon ok");
      } else {
        controller.println("#pjon nok");
      }
    }
    switch(command) {
    case 'i':
      controller.println("#comm ok");
      break;     
    case 'f':
      analogWrite(SPINDLE_PIN, 255);
      controller.println("#full spindle ok");
      break;

      break;
    case 't':
      analogWrite(SPINDLE_PIN, 128);
      controller.println("#half spindle ok");
      break;
   case 'o':
      analogWrite(SPINDLE_PIN, 0);
      controller.println("#spindle off ok");
      break;

    case 's':
    case 'S':
      controller.println("#mapped spindle ok");
      long param = atol(ptrParam);
      int i = map(param,0,20000,0,255);
      analogWrite(SPINDLE_PIN, i);
      controller.println("#ok");
      break;
    
    }
    for(int j=0;j<32;j++) { string[j]= 0; }
  }
}


bool sendCommand(int deviceId, int command, long param) {
  char cmd[10] = {0};
  pjonBus.update();
  ltoa(command,cmd,10);

  int len = strlen(cmd);

  int result=0;

  result = pjonBus.send_packet_blocking(deviceId, cmd, len);
  
  if (result!=PJON_ACK) {
    return false;
  }
  return sendParameter(deviceId, param);
  
}

bool sendParameter(int deviceId, long parameter) {
  char param[10] = {0};

  ltoa(parameter,param,10);

  int len = strlen(param);

  int result=0;

  result = pjonBus.send_packet_blocking(deviceId, param, len);

  return true;
  
}
