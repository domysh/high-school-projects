#include <iostream>
#include <ctime>
#include <stdlib.h>
#include <string>
#include <math.h>
using namespace std;

const string _RESET = "\u001B[0m";
const string _BLACK = "\u001B[30m";
const string _RED = "\u001B[31m";
const string _GREEN = "\u001B[32m";
const string _YELLOW = "\u001B[33m";
const string _BLUE = "\u001B[34m";
const string _PURPLE = "\u001B[35m";
const string _CYAN = "\u001B[36m";
const string _WHITE = "\u001B[37m";

string getColor(int c) {
    switch (c) {
        case 0:return _BLACK;
        case 1:return _RED; 
        case 2:return _GREEN; 
        case 3:return _YELLOW; 
        case 4:return _BLUE;
        case 5:return _PURPLE; 
        case 6:return _CYAN; 
        case 7:return _WHITE;
    }
    throw "INT NOT VALID!";
}


void alberoDiNatale(int altezza,int altezzaStelo,int larghezzaStelo,string printconst){
    if(altezza == 0)return;
    altezza--;
    altezza = abs(altezza);altezzaStelo = abs(altezzaStelo);larghezzaStelo = abs(larghezzaStelo);
    int Lstring = (altezza*2)+1;
    if(Lstring<larghezzaStelo)larghezzaStelo=Lstring;
    if(!(larghezzaStelo % 2))larghezzaStelo--;
    int Hstate = 1;
    int count = 0;
    while(Hstate<=Lstring){
        cout << _GREEN;
        cout << printconst ;
        int stop =( Lstring/2)-(Hstate/2);
        for(int i=0;i<stop;i++){
            cout << " " ;
        }
        for(int i=stop;i<Hstate+stop;i++){
            if(Hstate == 1)cout << _YELLOW<<"X--.."<<_RESET;
            else {
                if(count%((rand()%15)+5)==0) {
                    int c=0;
                    while(true) {
                        c = (rand()%6)+1;
                        if(c != 4 )break;
                    }
                   cout << getColor(c);
                    cout << "O";
                    cout << _GREEN;
                }else {
                    cout << "*" ;
                }
                count++;
            }
        }
        cout << endl;
        Hstate +=2;
    }
    
    int stop =(Lstring/2)-(larghezzaStelo/2);
    for(int c=0;c<altezzaStelo;c++){
        cout << _YELLOW;
        cout << printconst ;
        for(int i=0;i<stop;i++){
            cout << " " ;
        }
        for(int i=stop;i<larghezzaStelo+stop;i++){
            cout <<"*";
        }
        cout << endl;
    }
    cout<< _RESET;
}

int main(int argn,char** args){
    time_t now = time(NULL);
    tm* time = localtime(&now);
    if(time->tm_mon != 11)return 0;
    if(time->tm_mday == 31)return 0;
    
    int inp[] = {10,3,5};
    string inps = "      ";
	if(argn > 1){
        for(int i=1;i<argn&&i<4;i++)
		    inp[i-1] = stoi(args[i]);
    }
    if(argn >= 5){
        inps = string(args[4]);
    }
    srand(now);
    cout << endl;
    alberoDiNatale(inp[0],inp[1],inp[2],inps);
    cout << _RED << "\n|  --> Merry Christmas :) <--  |\n\n" << _RESET;
}
