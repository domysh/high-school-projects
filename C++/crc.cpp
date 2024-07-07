#include <iostream>
#include "vect.h"

string input(string out=""){
    string in;
    cout << out;
    getline(cin,in);
    return in;
}

vect<bool> getBits(string s){
    size_t bitcount = 0;
    for(char ele:s){
        if(ele == '0'  || ele == '1'){
            bitcount++;
        }else if(ele != ' '){
            throw invalid_argument("Not Valid String");
        }
    }

    vect<bool> res = vect<bool>(bitcount);
    int j = 0;
    for(int i=0;i<res.size();i++){
        while(s[j] == ' ')j++;
        res[i] = (s[j++] == '1');
    }
    return res;

}

void delZero(vect<bool> &v){
    int i;
    for(i=0;i<v.size();i++){
        if(v[i])break;
    }
    if(i == v.size()){v.clear();return;}
    v.trim(i,true);

}

vect<bool> inputBits(string ou, bool delZ = false){
    while(true){
        try{
            vect<bool> v = getBits(input(ou));
            if(delZ) delZero(v);
            return v;
        }catch(exception e){cout << "!> Inserire valori validi!\n";}
    }
}

void xorVectOp(vect<bool> &val, vect<bool> verBit){
    //cout << "OPERATION:" << val << endl;
    //int i,takePart;
    for(int i=0;i<verBit.size() && i<val.size();i++){
        val[i] ^= verBit[i];
    }
    /*
    takePart = val.ind(true);
    if(takePart>i || takePart == -1)takePart = i;
    val.trim(takePart,true);
    */
    delZero(val);
    //cout << val;
}

vect<bool> calc_crc(vect<bool> val, vect<bool> verBit){
    val = val.copy();
    delZero(val);
    val.resize(val.size()+(verBit.size()-1));
    while(true){
        //cout << val << endl;
        //cout << verBit << endl;
        if(verBit.size() > val.size()){
            return val;
        }
        xorVectOp(val,verBit);
    }
    
}

string tostringbit(vect<bool> v){
    string res = "";
    if(v.size() == 0) return "0";
    for(int i=0;i<v.size();i++){
        res += (v[i])?"1":"0";
    }
    return res;
}


int main(){
    vect<bool> bit = inputBits("Inserisci i valori binari da verificare:");
    vect<bool> verBit = inputBits("Inserisci i valori binari del polinomio generatore:",true);
    
    cout << "------------------------------------------" << endl;
    vect<bool> crc = calc_crc(bit,verBit);
    cout << "Bit di messaggio: " << tostringbit(bit) << endl;
    cout << "Bit polinomio generatore: " << tostringbit(verBit) << endl;
    cout << "Risulato divisione per CRC: " << tostringbit(crc) << endl;
    //Complete crc string
    crc = (vect<bool>(1) * (verBit.size()-crc.size()-1))+crc;
    crc = bit + crc;
    
    cout << "Messaggio da inviare con CRC: " << tostringbit(crc) << endl;
    cout << "------------------------------------------" << endl;
    


}


