#define PJON_PACKET_MAX_LENGTH 15
#include <PJON.h>

PJON<SoftwareBitBang> pjonBus;
#define PJON_PIN 11

int typeCounter=0;
char deviceBuffer[256];
int deviceBufferIndex=0;
char commandBuffer[256];
int commandBufferIndex=0;
char parameterBuffer[256];
int parameterBufferIndex=0;
char buffer[10];


void receiver_function(uint8_t *payload, uint16_t length, const PJON_Packet_Info &packet_info) {
  memset(buffer,0,10);
  memcpy(buffer,payload,length);      
  Serial.print("#");
  Serial.println(packet_info.sender_id);
  Serial.print(",");
  Serial.print(buffer);
  Serial.println(";");
  
}

void error_handler(uint8_t code, uint16_t data, void *custom_pointer) {
  Serial.print("#ERROR:");
  Serial.println(code);
  Serial.print(",");
  Serial.print(data);
  Serial.println(";");
}

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  
  pjonBus.strategy.set_pin(PJON_PIN);
  pjonBus.set_receiver(receiver_function);
  pjonBus.set_error(error_handler);
  pjonBus.set_synchronous_acknowledge(true);
  pjonBus.begin();

}

void loop() {
  pjonBus.update();
  pjonBus.receive();
  
  if (Serial.available()>0) {
      char c = Serial.read();
      if (c==',') {
        typeCounter++;
      } else if (c==';') {
        typeCounter=0;
        deviceBuffer[deviceBufferIndex]='\0';
        int deviceId = atol(deviceBuffer);
        sendCommand(deviceId);
        deviceBufferIndex=0;
        commandBufferIndex=0;
        parameterBufferIndex=0;      
      } else {
        switch (typeCounter) {
        case 0:
          deviceBuffer[deviceBufferIndex]=c;
          deviceBufferIndex++;
          break;
        case 1:
          commandBuffer[commandBufferIndex]=c;
          commandBufferIndex++;
          break;
        case 2:
          parameterBuffer[parameterBufferIndex]=c;
          parameterBufferIndex++;
          break;
        }
      }
  }
}

bool sendCommand(int deviceId) {

  bool result = pjonBus.send_packet_blocking(deviceId, commandBuffer, commandBufferIndex);
  if (result!=PJON_ACK) {
 //   return false;
  }
  Serial.print(deviceId);
  Serial.print(" cmdbuffer>");
  Serial.print(commandBuffer);
  Serial.print(" bufferLen:");
  Serial.println(commandBufferIndex);
  result = (pjonBus.send_packet_blocking(deviceId, parameterBuffer, parameterBufferIndex)==PJON_ACK);

  Serial.print(deviceId);
  Serial.print(" parambuffer>");
  Serial.print(parameterBuffer);
  Serial.print(" bufferLen:");
  Serial.println(parameterBufferIndex);

  return result;
}
