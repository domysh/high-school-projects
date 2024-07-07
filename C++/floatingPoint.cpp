#include <iostream>
#include <math.h>
#include <string>
#include "TerminalMenu.h"
using namespace std;

const wstring ANSI_RESET = L"\u001B[0m";
const wstring ANSI_BLACK = L"\u001B[30m";
const wstring ANSI_RED = L"\u001B[31m";
const wstring ANSI_GREEN = L"\u001B[32m";
const wstring ANSI_YELLOW = L"\u001B[33m";
const wstring ANSI_BLUE = L"\u001B[34m";
const wstring ANSI_PURPLE = L"\u001B[35m";
const wstring ANSI_CYAN = L"\u001B[36m";
const wstring ANSI_WHITE = L"\u001B[37m";

string remove(string s,char c){
    string res = "";
    for(char ch:s){if(ch!=c)res+=ch;}
    return res;
}
string formatBinString(string s,int limit,char add='0'){
    string res="";
    if(s.length()<=limit){
        for(int i=s.length();i<limit;i++){
            s=add+s;
        }
        res = s;
    }else{
        for(int i=s.length()-1;i>=0;i--){
            res = s[i] + res;
        }
    }
    return res;
}
string fromDecToBinStr(int num){
    string ret;
    if(num == 0) return "0";
    while(num>0){
        ret = (char)(num%2+'0') + ret;
        num = num/2;
    }
    return ret;
}
string fromFloToBinStr(long double num,int limit){
    //Scarto la parte intera
    num -= (int) num;
    //Inizio il calcolo
    string res="";
    for(int i=0;i<limit;i++){
        if(num!=0){
            num *=2;
            if((int) num==1){
                res+='1';
                num-= (int) num;
            }else{
                res+='0';
            }
        }else{
            res+='0';
        }
    }
    return res;
}
int calculateDotOffset(string bin){
    bool sub = false,oneFinded=false;
    int offsetPoint = 0;
    for(char c:bin){
        if(oneFinded && !sub && c!='.'){offsetPoint++;}
        else if(!oneFinded && sub && c!='1'){offsetPoint--;}
        else if(c == '1') if(sub){offsetPoint--;break;}else{oneFinded=true;}
        else if(c == '.') if(oneFinded){break;}else{sub = true;}
    }
    return offsetPoint;
}

string getMantissa(string bin,int limit){
    string res="";
    bool oneFinded = false;
    bin = remove(bin,'.');
    for(int i=0;i<limit+1;i++){
        if(i<bin.length()){
            if(oneFinded){
                res+=bin[i];
            }else{
                if(bin[i] == '1') oneFinded = true;
            }
        }else{
            res+='0';
        }
    }
    return res;
}

void makeResults(long double inp,int ex,int man){
    bool negative;int esp;
    //Memorizzo il segno
    if(inp<0) negative = true;
    else negative = false;
    inp = abs(inp);
    //Creo la stringa del risultato della conversione "Normale"
    string decimalRes = fromDecToBinStr((int) inp)+"."+ fromFloToBinStr(inp,man);
    esp = calculateDotOffset(decimalRes);
    cout << endl << "Traduzione in binario puro:" << endl << decimalRes << endl;
    cout << endl;
    string segno = ((negative)?"1":"0"),
    esponente = formatBinString(fromDecToBinStr(esp+(pow(2,ex)-1)),ex),
    mantissa = getMantissa(decimalRes,man);
    wcout << ANSI_RED;
    cout << "SEGNO:" <<  segno << endl;
    wcout << ANSI_RESET+ANSI_GREEN;
    cout << "ESPONENTE:" << esponente <<endl;
    wcout << ANSI_RESET+ANSI_BLUE;
    cout << "MANTISSA:" << mantissa << endl;
    wcout << ANSI_RESET << endl;

    cout << endl << "Risultato:" << endl;
    wcout << ANSI_RED;
    cout << segno;
    wcout << ANSI_RESET+ANSI_GREEN;
    cout << esponente;
    wcout << ANSI_RESET+ANSI_BLUE;
    cout << mantissa;
    wcout << ANSI_RESET << endl;
    SysPause();
}



void setValue(long double &v){
    try{
        cout << "Inserire Numero:";
        cin >> v;
    }catch(exception e){
        cout<< "-> Inserire un valore valido!\n";
        setValue(v);
        }
}

void setType(string &ty,int &ex,int &man){
    switch(MenuCreate({"- 32 bit","- 64 bit","- 128 bit"},"Selezionare la grandezza:\n\n")){
        case 0:
            ty = "32 bit";
            ex = 8;
            man = 23;
            break;
        case 1:
            ty = "64 bit";
            ex = 11;
            man = 52;
            break;
        case 2:
            ty = "128 bit";
            ex = 15;
            man = 112;
            break;
    }
}

void start(long double in,int ex,int man){
    if(ex != 0 && man!=0){
        makeResults(in,ex,man);
    }else{
        cout << "Selezionare una codifica prima!\n";
        SysPause();
    }


}

int main(){
    long double inp=0;string type = "None";
    int esponente=0,mantissa=0;
    string voices[] = {"","",">> Encode!","X> Exit"};
    while(true){
        voices[0] = "?> Valore: "+to_string(inp);
        voices[1] = "?> Codifica: "+type;
        switch(MenuCreate(voices,4,"\n---> IEEE 754 ENCODER <---\n\n")){
            case 0:
                setValue(inp);
                break;
            case 1:
                setType(type,esponente,mantissa);
                break;
            case 2:
                start(inp,esponente,mantissa);
                break;
            case 3:
                return 0;
        }
    }

}