#include "Adafruit_VL53L0X.h"

Adafruit_VL53L0X lox = Adafruit_VL53L0X();
long highVal=0;

void setup() {
  Serial.begin(115200);

  // wait until serial port opens for native USB devices
  while (! Serial) {
    delay(1);
  }
  
  Serial.println("Adafruit VL53L0X test");
  if (!lox.begin(0x50, false)) {
    Serial.println(F("Failed to boot VL53L0X"));
    while(1);
  }
  // power 
  lox.configSensor(lox.VL53L0X_SENSE_HIGH_ACCURACY);
  Serial.println(F("VL53L0X API Simple Ranging example\n\n")); 
  highVal = averageOf(10);
  Serial.println("Setup complete");
  Serial.println(highVal);
}

void loop() {
    
//  Serial.print("Reading a measurement... ");
//  lox.rangingTest(&measure, false); // pass in 'true' to get debug data printout!
/*  lox.getSingleRangingMeasurement(&measure, false); // pass in 'true' to get debug data printout!
  if (measure.RangeStatus != 4) {  // phase failures have incorrect data
    //Serial.print("Distance (mm): "); 
    Serial.println(measure.RangeMilliMeter);
  } else {
    Serial.println(" out of range ");
  }
//  delay(100);
*/
  long val = averageOf(1);
  if (abs(highVal-val)>2) {
    Serial.println("EdgeDetected");
    Serial.println(val);
  }

}


unsigned long averageOf(int avg) {
  unsigned long added=0;
  VL53L0X_RangingMeasurementData_t measure;
  for(int i=0;i<avg;i++) {
    lox.getSingleRangingMeasurement(&measure, false);
    //lox.waitRangeComplete();
    added += measure.RangeMilliMeter;
  }
  return added/avg;
}
