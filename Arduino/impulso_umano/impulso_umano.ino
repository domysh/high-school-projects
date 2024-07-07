#define pulsante 12
#define led 13
int timeWait= 0;
bool stopVar = false;

void start(){
  delay(random(1000,5000));
  digitalWrite(led,HIGH);
}


void setup(){
  pinMode(OUTPUT,led);
  pinMode(INPUT,pulsante);
  Serial.begin(9600);
  start();
}

void loop(){
  if(stopVar){
    if(digitalRead(pulsante)){
      stopVar = false;
      timeWait = 0;
      delay(1000);
      start();
    }
    return;
  } 
  if(!digitalRead(pulsante)){
    timeWait++;
    delay(1);
  }else{
    delay(10);
    digitalWrite(led,LOW);
    Serial.print("Millisecondi attesi:");
    Serial.println(timeWait);
    Serial.println("Questa è la tua velocità di reazione.");
    Serial.println("Prova a fare di meglio cliccando il pulsante.");
    stopVar = true;
  }
}
