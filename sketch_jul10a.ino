#include <Servo.h>

Servo servo;
int servoPin = 8;
int ledPin = 13;

String command;

void setup() {
  Serial.begin(9600);

  pinMode(ledPin,OUTPUT);
  
  servo.attach(servoPin);
  unlock();
}

void loop() {
  digitalWrite(ledPin,HIGH);
  delay(1000);
  digitalWrite(ledPin,LOW);
  delay(1000);
}

void serialEvent() {
    command = Serial.readString();

    if(command.equals("unlock")) {
      unlock();
    } else if(command.equals("lock")) {
      lock();
    }
}

void lock() {
  servo.write(180);
  delay(1000);
}

void unlock() {
  servo.write(-180);
  delay(1000);
}

