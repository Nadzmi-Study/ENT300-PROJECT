int ledPin = 13;

void setup() {
  Serial.begin(9600);

  pinMode(ledPin,OUTPUT);
  digitalWrite(ledPin,LOW);
}

void loop() {
  String command = Serial.readString();

  if(command == "on") digitalWrite(ledPin,HIGH);
  else if(command == "off") digitalWrite(ledPin,LOW);
}

