#include <Servo.h>
#include <String.h>
/*DEFINIAMO I PULSANTI E MOTORI*/
Servo motore1, motore2, motore3, motore4, motore5, motore6;/*, motore7, motore8; */
int pulsante1 = 53;
int pulsante2 = 52;
int lucON = 51;
int lucOFF = 50;
/*DEFINIZIONE DI VARIABILI VARIE*/
int mode=0;
int v1=3;/*v1=250*/
long m1=0;
int g1=0;
int s1=0;
int v2=6;/*v2=204*/
long m2=0;
int g2=0;
int s2=0;
int v3=12;/*v3=185*/
long m3=0;
int g3=0;
int s3=0;
int v4=18;/*v4=164*/
long m4=0;
int g4=0;
int s4=0;
int v5=23;/*v5=125*/
long m5=0;
int g5=0;
int s5=0;
int v6=27;/*v6=112*/
long m6=0;
int g6=0;
int s6=0;
int v7=30;/*v7=102*/
long m7=0;
int g7=0;
int s7=0;
int v8=36;/*v8=97*/
long m8=0;
int g8=0;
int s8=0;
/*
 * mode:
 * 0= FERMO
 * 1 = 1pianeta etc...
 * 8 = TUTTO ACCESO
*/
void setup() {
motore1.attach(3);
motore2.attach(5);
motore3.attach(6);
motore4.attach(9);
motore5.attach(10);
motore6.attach(11);
/*
motore7.attach();
motore8.attach();
*/
pinMode(pulsante1,INPUT);
pinMode(pulsante2,INPUT);
Serial.begin(9600);
}
void loop() {
  /*Controllo pulsante 1*/
while(digitalRead(pulsante1)==1){
  if(mode==8){
  mode=0;
  }else{
   mode++;
  }
  delay(300);
  }
  /*Controllo pulsante 2*/
while(digitalRead(pulsante2)==1){
  if(mode<=8 and mode!=0){
  mode=0;
  }else{
   mode=8;
  }
  delay(300);
  }
/*Scrittura varie modalità*/
  if(g1==180){
    s1=1;
  }else if(g1==0){
    s1=0;
  }
  
  if(millis() - m1 > v1 and mode>=1){
  m1=millis();
  if(s1==0){
   motore1.write(g1);
   g1++;
  }else if(s1==1){
    motore1.write(g1);
   g1--;
  }
}
  if(millis() - m2 > v2 and mode>=2){
  m2=millis();
  if(s2==0){
   motore2.write(g2);
   g2++;
  }else if(s2==1){
    motore2.write(g2);
   g2--;
  }
}
  if(g3==180){
    s3=1;
  }else if(g3==0){
    s3=0;
  }
  if(millis() - m3 > v3 and mode>=3){
  m3=millis();
  if(s3==0){
   motore3.write(g3);
   g3++;
  }else if(s3==1){
    motore3.write(g3);
   g3--;
  }
}
  if(g4==180){
    s4=1;
  }else if(g4==0){
    s4=0;
  }
  if(millis() - m4 > v4 and mode>=4){
  m4=millis();
  if(s4==0){
   motore4.write(g4);
   g4++;
  }else if(s4==1){
    motore4.write(g4);
   g4--;
  }
}
  if(g5==180){
    s5=1;
  }else if(g5==0){
    s5=0;
  }
  if(millis() - m5 > v5 and mode>=5){
  m5=millis();
  if(s5==0){
   motore5.write(g5);
   g5++;
  }else if(s5==1){
    motore5.write(g5);
   g5--;
  }
}
  if(g6==180){
    s6=1;
  }else if(g6==0){
    s6=0;
  }
  if(millis() - m6 > v6 and mode>=6){
  m6=millis();
  if(s6==0){
   motore6.write(g6);
   g6++;
  }else if(s6==1){
    motore6.write(g6);
   g6--;
  }
}
  if(g3==180){
    s3=1;
  }else if(g3==0){
    s3=0;
  }
  if(millis() - m3 > v3 and mode>=3){
  m3=millis();
  if(s3==0){
   motore3.write(g3);
   g3++;
  }else if(s3==1){
    motore3.write(g3);
   g3--;
  }
}
  if(mode>0){
  digitalWrite(lucON,HIGH);
  digitalWrite(lucOFF,LOW);
  }else{
  digitalWrite(lucON,LOW);
  digitalWrite(lucOFF,HIGH);
  }
}
