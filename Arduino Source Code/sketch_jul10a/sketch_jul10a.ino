// libraries used
#include <Servo.h>

// variable declaration
Servo servo;
int servoPin = 8;
int ledPin = 13;

void setup() {
  Serial.begin(9600); // begin serial comm

  pinMode(ledPin,OUTPUT); // initialize led
  
  servo.attach(servoPin); // initialize servo
  
  unlock(); // unlock(for 1st time user)
}

void loop() {
  // blinking led means it is waiting for user input
  digitalWrite(ledPin,HIGH);
  delay(1000);
  digitalWrite(ledPin,LOW);
  delay(1000);
}

void serialEvent() { // listen for external event
    String command = Serial.readString(); // get command from serial comm

    // do operation based on command
    if(command.equals("unlock")) {
      unlock();
    } else if(command.equals("lock")) {
      lock();
    }
}

void lock() { // lock() function
  servo.write(180); // turn servo 180 deg clockwise
  delay(1000);

  Serial.println("Door locked"); // print result
}

void unlock() { // unlokck() function
  servo.write(-180); // turn servo 180 deg anti-clockwise
  delay(1000);

  Serial.println("Door unlocked"); // print result
}

