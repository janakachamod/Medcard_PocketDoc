#include <MQUnifiedsensor.h>
#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
 
#define FIREBASE_HOST "finaltest-3f641-default-rtdb.firebaseio.com" // Firebase host
#define FIREBASE_AUTH "P0US4MCynFjxxonkqZ4WtYu1BoydqGXYzlPtr3Os" //Firebase Auth codehttps://finaltest-3f641-default-rtdb.firebaseio.com/
#define WIFI_SSID "SLT-ADSL057C9C" //Enter your wifi Name
#define WIFI_PASSWORD "63162837" // Enter your password

#include <MQUnifiedsensor.h>
#define         Board                   ("ESP8266")
#define         Pin                     (A0)  
#define         Type                    ("MQ-135") 
#define         Voltage_Resolution      (3.3) 
#define         ADC_Bit_Resolution      (10) 
#define         RatioMQ135CleanAir        (3.6)
#define         Pin1                    (D1)
//#define calibration_button 13 


MQUnifiedsensor MQ135(Board, Voltage_Resolution, ADC_Bit_Resolution, Pin, Type);
const int fanPin = D2; // Pin connected to the fan
int ENA = D7;

void setup() {
  
  Serial.begin(9600); 
 
   
   WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.println("Connected.");
  Serial.println(WiFi.localIP());
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);

  Firebase.set("CO", 0);
  Firebase.set("Alcohol", 0);
  Firebase.set("CO2", 0);
  Firebase.set("Toluen", 0);
  Firebase.set("NH4", 0);
  Firebase.set("Aceton", 0);
  Firebase.set("FAN",0);
  
  MQ135.setRegressionMethod(1); 
  MQ135.init(); 
  Serial.print("Calibrating please wait.");
  float calcR0 = 0;
  for(int i = 1; i<=10; i ++)
  {
    MQ135.update(); 
    calcR0 += MQ135.calibrate(RatioMQ135CleanAir);
    Serial.print(".");
  }
  MQ135.setR0(calcR0/10);
  Serial.println("  done!.");
  
  if(isinf(calcR0)) {Serial.println("Warning: Conection issue, R0 is infinite (Open circuit detected) please check your wiring and supply"); while(1);}
  if(calcR0 == 0){Serial.println("Warning: Conection issue found, R0 is zero (Analog pin shorts to ground) please check your wiring and supply"); while(1);}
  
  Serial.println("** Values from MQ-135 ****");
  Serial.println("|    CO   |  Alcohol |   CO2  |  Toluen  |  NH4  |  Aceton  |"); 
  pinMode(fanPin, OUTPUT); // Set fanPin as an output
   pinMode(ENA, OUTPUT);
  digitalWrite(fanPin, HIGH); // Turn off the fan initially 
}

void loop() {
  digitalWrite(ENA, HIGH);
  MQ135.update(); 

  MQ135.setA(605.18); MQ135.setB(-3.937); 
  float CO = MQ135.readSensor(); 

  MQ135.setA(77.255); MQ135.setB(-3.18); 
  float Alcohol = MQ135.readSensor(); 

  MQ135.setA(110.47); MQ135.setB(-2.862); 
  float CO2 = MQ135.readSensor(); 

  MQ135.setA(44.947); MQ135.setB(-3.445); 
  float Toluen = MQ135.readSensor(); 
  
  MQ135.setA(102.2 ); MQ135.setB(-2.473); 
  float NH4 = MQ135.readSensor(); 

  MQ135.setA(34.668); MQ135.setB(-3.369); 
  float Aceton = MQ135.readSensor(); 
  Serial.print("CO "); Serial.print(CO);
  Firebase.setFloat("CO",CO);
  Serial.print(" Alcohol "); Serial.print(Alcohol);
  Firebase.setFloat("Alcohol",Alcohol);
   Serial.print(" CO2  "); Serial.print(CO2 + 400); 
   Firebase.setFloat("CO2",CO2+400);
  Serial.print(" Toluen  "); Serial.print(Toluen);
  Firebase.setFloat("Toluen",Toluen); 
  Serial.print(" NH4   "); Serial.print(NH4); 
  Firebase.setFloat("NH4",NH4);
  Serial.print(" Aceton "); Serial.print(Aceton);
  Firebase.setFloat("Aceton",Aceton);
  Serial.println(""); 
   // Check conditions and control the fan
  if (CO > 50 || Alcohol > 100 || CO2+400 > 500)
  {
    digitalWrite(fanPin, HIGH); // Turn on the fan
    Firebase.setInt("FAN", 1); // Set fan status in Firebase
  }
  else
  {
    digitalWrite(fanPin, LOW); // Turn off the fan
    Firebase.setInt("FAN", 0); // Set fan status in Firebase
  }

  delay(1000);
  
  
}
