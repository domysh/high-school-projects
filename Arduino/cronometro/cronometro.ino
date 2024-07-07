int pul = 12;
int luc = 9;
int luc2= 10;
int pull= 0;
long num= 0;
int numn= 0;
int st=0;
long sec=0;
long minu=0;
long mill=0;
void setup(){
pinMode(OUTPUT,luc);
pinMode(OUTPUT,luc2);
pinMode(INPUT,pul);
Serial.begin(230400);
}

void loop(){
pull=digitalRead(pul);
if(pull==1 and numn==0){
  st=1;
  digitalWrite(luc,HIGH);
  digitalWrite(luc2,HIGH);
num++;
  delay(1);
minu= num/60000;
sec= num/1000-minu*60;
long t= minu*6000+sec*100;
mill= num/10-t;

  Serial.print(minu);
  Serial.print(":");
  Serial.print(sec);
  Serial.print(":");
  Serial.print(mill);
  Serial.println(" Time;");
  Serial.println("***************");
} else if(pull==0){
  if(numn==0 and st==1){
    Serial.println("FINE");
    Serial.println("Clicca il pulsante RESET per iniziare di nuovo a cronometrare.");
    numn=1;
  }
    digitalWrite(luc,LOW);
  digitalWrite(luc2,LOW);
  num=0;
}
}
