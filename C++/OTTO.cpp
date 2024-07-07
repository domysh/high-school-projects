#include <iostream>
#include <stdlib.h>
#include <time.h>
#include "TerminalMenu.h"
#include <vector>
#include <string>
#include <sstream>
#include <algorithm>
#include <iterator>
using namespace std;
const int SPACER = 13;
string ruote[] = {
        "Bari",
        "Cagliari",
        "Firenze",
        "Genova",
        "Milano",
        "Napoli",
        "Palermo",
        "Roma",
        "Torino",
        "Venezia"
} ;
const int LENX = 10, LENY = 5,
		MIN_GEN = 1, MAX_GEN = 90,
		MAX_NUMTOPLAY = 10;


bool isInVet(int* v,int len, int tofind){
    for(int i=0;i<len;i++){
        if (v[i] == tofind){
            return true;
        }
    }
    return false;
}
bool isInVet(string* v,int LEN,string s){
	for(int i=0;i<LEN;i++){
		if(v[i]==s){
			return true;
		}
	}
	return false;
}

template <class tipo>
bool isInVet(vector<tipo> v,tipo s){
	for (int i=0;i< v.size();i++){
		if(v[i]== s){
			return true;
		}
	}
	return false;

} 
 
template <class Container>
void split(const std::string& str, Container& cont, char delim = ' ')
{
    std::stringstream ss(str);
    std::string token;
    while (std::getline(ss, token, delim)) {
        cont.push_back(token);
    }
}

void riempi_matrice(int ** mat,int lx,int ly,int da, int a){
    for(int i=0;i<lx;i++){
        for(int j=0;j<ly;j++){
            while(true){
                mat[i][j] = rand()%(a-da+1)+da;
				if(!isInVet(mat[i],j,mat[i][j])){
					break;
				}
            }
        }
    }
}

int getNNum(int n){
    int count = 1;
    while(n >= 10){
        n = n/10;
        count++;
    }
    return count;
}

template <class tipo>
void print(tipo * v,int len){
	for(int i=0;i<len;i++){
		cout << v[i];
	}
}
template <class tipo>
void print(vector<tipo> v){
	for(int i=0;i<v.size();i++){
		cout << v[i];
	}
}

void stampaVet(int *v,int len,string n){
    cout <<"  "<< n << ' ';
    for (int i=0;i< (SPACER - n.length());i++) cout << ' ';
    for(int i=0;i<len;i++){
        cout << v[i] << "   ";
        if(getNNum(v[i])==1) cout << ' ';
    }
    cout << endl;
}
void stampaMat(int **m,int lx,int ly){
    for(int i=0;i<lx;i++){
        stampaVet(m[i],ly,ruote[i]);
    }
}


int** genResult(){
    int ** mat = (int **)malloc(sizeof(int*)*LENX);
    
    for (int i=0;i<LENX;i++){
        mat[i] = (int *)malloc(sizeof(int)*LENY);
    }
    
    riempi_matrice(mat,LENX,LENY,MIN_GEN,MAX_GEN);
    return mat;
}

void printResult(int** mat){
    cout << endl << "Ecco le ruote generate!" << endl << endl;
    stampaMat(mat,LENX,LENY);
    cout << endl;
}

int getNum(string phrase){
	int num;
	string numstr;
	cout << phrase << ':';
	getline(cin,numstr);
	try{
		return stoi(numstr);
	}catch(exception e){
		cout << "!> Inserire valore valido" << endl;
		return getNum(phrase);
	}
}

string primaGiocata(int** res,int num){
	string vittorie = "";
	for(int i=0;i<LENX;i++){
		if(num == res[i][0]){
			vittorie += ruote[i]+'|';
		}
	}
	return vittorie;
}

template <class tipo>
int getIndex(tipo* v,int LEN,tipo ele){
	for(int i=0;i<LEN;i++){
		if(ele == v[i]){
			return i;
		}
	}
	return -1;
}

string giocata(int ** mat,vector<string> selRuote,vector<int> selNum){
	string res = "";
	for(string this_r:selRuote){
		int i = getIndex(ruote,LENX,this_r);
		res+= this_r;
		for(int j=0;j<LENY;j++){
			if(isInVet(selNum,mat[i][j])){
				res+="."+to_string(mat[i][j]);
			}
		}
		res+="|";
	}
	return res;
}
string to_string(string s){return s;}

template <class tipo>
string getOneString(vector<tipo> v){
	string res ="";
	for(int i = 0;i<v.size();i++){
		res+= to_string(v[i]);
		if(i != (v.size()-1)) res +=" | ";
	}
	return res;
}


void sel_ruota(vector<string> &selR){
	vector<string> toShow;
	for(int i=0;i<LENX;i++){
		if(!isInVet(selR,ruote[i])){
			toShow.push_back(ruote[i]);
		}
	}
	if(toShow.size() == 0){
		if(MenuCreate({"Non fare niente","Deseleziona Tutto"},"\nTutte le ruote sono già selezionate!\n\n") == 1){
			selR.clear();
		}
	}else{
		toShow.push_back("----------");
		toShow.push_back("Tutte le Ruote");
		toShow.push_back("Deseleziona tutto");
		toShow.push_back("Niente");
		int sel = MenuCreate(toShow,"\nSeleziona la ruota su cui giocare\n\n");
		if(sel<(toShow.size()-3)){
			selR.push_back(toShow[sel]);
		}else{
			if(sel == toShow.size()-3){
				selR.clear();
				for(string s:ruote){
					selR.push_back(s);
				}
			}else if(sel == toShow.size()-2){
				selR.clear();
			}
		}
	}
}

void sel_num(vector<int> &sel){
	bool fine = false;
	while(!fine){
		if(sel.size()>=MAX_NUMTOPLAY){
			if(MenuCreate({"Non fare niente","Deseleziona Tutto"},"\nHai raggiunto il massimo dei numeri da giocare!\n\n")==1){
					sel.clear();
			}else{
				fine = true;
			}
		}else{
			switch(MenuCreate({"Inserisci numero","Non fare niente","Deseleziona Tutto"},"\nScegli l'opzione desiderata!\n\n")){
				case 0:{
						int n;
						while(true){
							n = getNum("Inserisci il numero da giocare");
							if (!isInVet(sel,n)){
								if(n>=MIN_GEN && n<= MAX_GEN){
									break;
								}else{
									cout << "Il numero deve esser compreso tra " << MIN_GEN << " e " << MAX_GEN << endl;
								}
							}else{
								cout << "Numero già giocato" << endl;
							}
						}
						sel.push_back(n);
						break;}
				case 1:{fine = true;break;}
				case 2:{sel.clear();fine = true;break;}
			}
		}
	}
}


vector<string> split(string s,char d){//string*
	vector<string> v;
	split(s,v,d);
	return v;
}

void gioca(int** mat){
	bool next = true;
	vector<string> selected;
	vector<int> numsel;
	string r_sel,n_sel;
	while(next){
		if (selected.size() == LENX){
			r_sel = "Tutte le Ruote";
		}else if(selected.size() == 0){
			r_sel = "Nessuna!";
		}else{
			r_sel = getOneString(selected);
		}

		if(numsel.size() == 0){
			n_sel = "Nessun numero!";
		}else{
			n_sel = getOneString(numsel);
		}
		r_sel = "\n------------------------------\nInserisci i dati della giocata\n------------------------------\n\nRuote: "+r_sel+"\nNumeri: "+n_sel+"\n\n";
		switch(MenuCreate({"Inserimento Ruote","Inserimento Numeri","Gioca 'Prima Scelta'","Gioca!","Back"},r_sel)){
			case 0:{cout << endl;
					sel_ruota(selected);
					break;}
			
			case 1:{cout << endl;
					sel_num(numsel);
					break;}

			case 3:{
					if(selected.empty() || numsel.empty()){
						cout << "Inserire almeno 1 ruota e 1 numero" << endl;
						SysPause();
					}else{
						vector<string> risultati = split(giocata(mat,selected,numsel),'|');
						//print(risultati);
						bool win = false;
						for(int i=0;i<risultati.size();i++){
							
							vector<string> vittorie = split(risultati[i],'.');
							//print(vittorie);
							if(vittorie.size() > 1){
								win = true;
								cout << "Vittoria su: " << vittorie[0] << endl;
								cout << "Con i numeri:";
								for (int c=1;c<vittorie.size();c++){
									cout << " " << vittorie[c];
								}
								cout << endl << endl;
							}
							
						}
						if(!win){
							cout << "\n-->  Hai Perso :(\n"; 
						}else{
							cout << "\n-->  Hai Vinto :)\n";
						}
						
						SysPause();
						next = false;
					}
					break;}
			case 2:{
					int n;
					while(true){
							n = getNum("Inserisci il numero da giocare");
							if(n>=MIN_GEN && n<= MAX_GEN){
								break;
							}else{
								cout << "Il numero deve esser compreso tra " << MIN_GEN << " e " << MAX_GEN << endl;
							}
						}
					string res = primaGiocata(mat,n);
					vector<string> vitt = split(res,'|');
					if(vitt.size()>0){
						cout << "Hai vinto sulle ruote: " << getOneString(vitt);
					}else{
						cout << "\n-->  Hai Perso :(\n";
					}
					SysPause();
					next = false;break;}
			case 4:{next = false; break;}

		}

}



}
int main(){
	srand(time(NULL));
	int** generati = genResult();
	while(true){
	switch(MenuCreate({"Genera Ruote!","Mostra Ruote!","Gioca!","Exit"},"\n-------------------------\n   IL GIOCO DELL'OTTO\n-------------------------\n\n")){
		case 0:{generati = genResult();
			cout << "\nNuova ruota generata!\n";
			SysPause(); break;}

		case 1:{printResult(generati);
				SysPause(); break;}
		
		case 2:{//int num[] = {12,54,23,5,31,34,4,2,3}; 
				//cout << giocata(generati,ruote,num,9) << endl;
				gioca(generati);break;}
		
		case 3:{return 1;}
	}
	}
	//int num = getNum("Ciao Inserisci qualcosa!");
	//printResult(generati);
	//cout << giocata1(generati,num);
	cout << endl;

}
