#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
 
#define FIREBASE_HOST "finaltest-3f641-default-rtdb.firebaseio.com" // Firebase host
#define FIREBASE_AUTH "P0US4MCynFjxxonkqZ4WtYu1BoydqGXYzlPtr3Os" //Firebase Auth codehttps://finaltest-3f641-default-rtdb.firebaseio.com/
#define WIFI_SSID "SLT-ADSL057C9C" //Enter your wifi Name
#define WIFI_PASSWORD "63162837" // Enter your password


const int sensorPin1 = D2;  // TCRT5000 sensor 1
const int sensorPin2 = D3;  // TCRT5000 sensor 2
const int buzzerPin = D1;   // Buzzer module
const int relayPin = D5;    // Relay module

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
  
  pinMode(sensorPin1, INPUT);
  pinMode(sensorPin2, INPUT);
  pinMode(buzzerPin, OUTPUT);
  pinMode(relayPin, OUTPUT);

  Firebase.set("sensorpin1", 0);
  Firebase.set("sensorpin2", 0);
  Firebase.set("buzzer", 0);
  Firebase.set("relay", 0);
  
}

void loop() {
  int sensorValue1 = digitalRead(sensorPin1);
  int sensorValue2 = digitalRead(sensorPin2);

  // If both sensors detect an obstacle
  if (sensorValue1 == HIGH && sensorValue2==HIGH) {
    digitalWrite(buzzerPin, HIGH);  // Turn on the buzzer
    digitalWrite(relayPin, HIGH); 
    Firebase.setInt("buzzer",1);// Activate the relay
     Firebase.setInt("relay",1);
  } 
  else  {
    digitalWrite(buzzerPin, LOW);   // Turn off the buzzer
    digitalWrite(relayPin, LOW);    // Deactivate the relay
     Firebase.setInt("buzzer",0);
     Firebase.setInt("relay",0);
  }
}
