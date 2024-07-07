int pul = 12;
int luc = 9;
int luc2= 10;
int pull= 0;
void setup(){
pinMode(OUTPUT,luc);
pinMode(OUTPUT,luc2);
pinMode(INPUT,pul);
}

void loop(){
pull=digitalRead(pul);
if(pull==1){
  digitalWrite(luc2,LOW);
  digitalWrite(luc,HIGH);
  delay(200);
  digitalWrite(luc,LOW);
  digitalWrite(luc2,HIGH);
  delay(200);
}
if(pull==0){
  digitalWrite(luc,LOW);
  digitalWrite(luc2,LOW);
}
}
