#include <iostream>
#include <stdlib.h>
#include <time.h>
#include "TerminalMenu.h"

using namespace std;

const char NOT_SET=' ';
const int PARITY_SET=10;

const int combinationWin[8][3][2] = {
                    { {0,0},{0,1},{0,2} },
                    { {1,1},{1,0},{1,2} },
                    { {2,2},{2,0},{2,1} },
                    { {0,0},{1,0},{2,0} },
                    { {1,1},{0,1},{2,1} },
                    { {2,2},{0,2},{1,2} },
                    { {1,1},{0,0},{2,2} },
                    { {1,1},{0,2},{2,0} }
                };

int arrayFull=0;
char tris[3][3]; // ' ' 'X' 'O'
bool win = false;
bool meWin = false;

void resetTris(){
	
    for(int i=0;i<3;i++){
        for(int j=0;j<3;j++){
            tris[i][j] = NOT_SET;
        }
    }
    win = false;
    meWin = false;
    arrayFull = 0;
}

void upperString(string &s){
    for(auto & c: s) c = toupper(c);
}

int* createVectorCoord(int x,int y){
	int *c = new int[2];
	c[0] = x;
	c[1] = y;
	return c;
}

int* getIndFromCoord(string coord){
    int *c = new int[2];
    c[0]= (coord[0] - 'A');
    c[1]=(coord[1] - '1');
    return c;
}

bool isPosFull(const int c[2]){return tris[c[0]][c[1]]!=NOT_SET;}
bool isPosFull(string coord){return isPosFull(getIndFromCoord(coord));}
bool isPosFull(int x, int y){return isPosFull(createVectorCoord(x,y));}

string inputCoordinates(string req,string adv){
    string mossa;
    bool ctl = false;
        while(true){
            cout << "\n"<< adv <<"> "<< req;
            cin >> mossa;
            upperString(mossa);
            if(mossa.length()==2){
                if(mossa[0]>='A' && mossa[0]<='C' && mossa[1]>='1' && mossa[1]<='3'){
                    ctl=false;
                }else{
                    ctl=true;
                }
            }else{
                ctl=true;
            }
            if(ctl){
                cout<< "\n!!!> Inserire una cordinata valida! (\"A1\")\n";
            }else{
                if(isPosFull(mossa)){
                    cout << "\n!!!> Posizione gia occupata!\n";
                }else{
                    return mossa;
                }
            }
                
        }
    return mossa;
}

void setTris(char what,const int c[2]){
    bool oldPos = isPosFull(c);
    tris[c[0]][c[1]] = what;
    if(what != NOT_SET && !oldPos){
        arrayFull++;
    }else if(what == NOT_SET && oldPos){
        arrayFull--;
    }
}
void setTris(char what,string s){setTris(what,getIndFromCoord(s));}
void setTris(char what,int x,int y){setTris(what,createVectorCoord(x,y));}

void printTris(){
    clearSc();
    cout << endl;
    cout << "       1   2   3  " << endl<< endl;
    cout << "       "<< tris[0][0] <<" | "<< tris[0][1] <<" | "<< tris[0][2]<< endl;
    cout << "  A   ___|___|___" << endl;
    cout << "       "<< tris[1][0] <<" | "<< tris[1][1] <<" | "<< tris[1][2] << endl;
    cout << "  B   ___|___|___" << endl;
    cout << "       "<< tris[2][0] <<" | "<< tris[2][1] <<" | "<< tris[2][2] << endl;
    cout << "  C      |   |   " << endl;
}

bool threeTrisCtl(int x1,int y1,int x2,int y2,int x3,int y3){
    return ((tris[x1][y1] == tris[x2][y2]) && (tris[x2][y2] == tris[x3][y3]) && (tris[x1][y1] != NOT_SET));
}
bool threeTrisCtl(const int comb[3][2]){
    int a,b,c,d,e,f;
    a = comb[0][0];b = comb[0][1];
    c = comb[1][0];d = comb[1][1];
    e = comb[2][0];f = comb[2][1];
    return threeTrisCtl(a,b,c,d,e,f);
}

bool onWin(char userkey='X'){
    //verifica di tutte le combinazioni
    if(tris[0][0] != NOT_SET && !win){
        if(threeTrisCtl(combinationWin[0]))win=true; //
        if(threeTrisCtl(combinationWin[3]))win=true;
        if(tris[0][0] == userkey && win)meWin=true;
    }
    if(tris[1][1] != NOT_SET && !win){
        if(threeTrisCtl(combinationWin[1]))win=true;
        if(threeTrisCtl(combinationWin[4]))win=true;
        if(threeTrisCtl(combinationWin[6]))win=true; //ctl obl
        if(threeTrisCtl(combinationWin[7]))win=true; // ctl obl
        if(tris[1][1] == userkey && win)meWin=true;
    }
    if(tris[2][2] != NOT_SET && !win){
        if(threeTrisCtl(combinationWin[2]))win=true;
        if(threeTrisCtl(combinationWin[5]))win=true;
        if(tris[2][2] == userkey && win)meWin=true;
    }
    return win;
}
//---------------------------------------------------------------
//Inteligenza artificiale
//---------------------------------------------------------------
int* valueOfCurrent(int cMe,int cYou){
    // -1 'impossible to win' 0 'free' 1 'one sign' 2 'two sign'
    if((cMe == 0) ^ (cYou == 0)) // Se non è vuoto ed è possibile vincere
        if(cMe == 0)
            return createVectorCoord(-1,cYou);
        else
            return createVectorCoord(cMe,-1);
    else if((cMe==0) && (cYou==0)) // se e vuoto
        return createVectorCoord(0,0);
    else // se è impossibile vincere in assoluto
        return createVectorCoord(0,0);
}

int* valueOfCombination(int x1,int y1,int x2,int y2,int x3,int y3,char userSym){
    int cMe=0,cYou=0;
    if(isPosFull(x1,y1)){
        if(tris[x1][y1] == userSym) cMe++;
        else cYou++;
    }
    if(isPosFull(x2,y2)){
        if(tris[x2][y2] == userSym) cMe++;
        else cYou++;
    }
    if(isPosFull(x3,y3)){
        if(tris[x3][y3] == userSym) cMe++;
        else cYou++;
    }
    return valueOfCurrent(cMe,cYou);
}

int* valueOfCombination(const int comb[3][2],char userSym){
    int a,b,c,d,e,f;
    a = comb[0][0];b = comb[0][1];
    c = comb[1][0];d = comb[1][1];
    e = comb[2][0];f = comb[2][1];
    return valueOfCombination(a,b,c,d,e,f,userSym);
}

int getIndOfValue(const int vet[8],int what){
    for(int i=7;i>=0;i--){
        if(vet[i] == what){
            return i;
        }
    }
    return -1;
}

bool blockOrWin(const int arr[8],char sym){
    int ind = getIndOfValue(arr,2);
    if(ind != -1){
        for(int i=0;i<3;i++){
            if(!isPosFull(combinationWin[ind][i])){
                setTris(sym,combinationWin[ind][i]);
                return true;
            }
        }
    }
    return false;
}

bool find2FreePoints(int idComb,int *p1,int *p2){
    p1[0]=-1;p1[1]=-1;p2[0]=-1;p2[1]=-1;
    for(int i = 0; i<3;i++){
        if(!isPosFull(combinationWin[idComb][i])){
            if(p1[0] == -1){
                p1[0] = combinationWin[idComb][i][0];
                p1[1] = combinationWin[idComb][i][1];
            }else{
                p2[0] = combinationWin[idComb][i][0];
                p2[1] = combinationWin[idComb][i][1];
                return true;
            }
        }
    }
    return false;
}
bool isPointInCombination(const int p[2],int comb){
    for(auto point : combinationWin[comb]){
        if(point[0]==p[0] && point[1]==p[1]) return true;
    }
    return false;
}

bool pointGrantValue(const int p[2],const int vet[8],char sym){
    for(int i=0;i<8;i++){
        if(vet[i] == 1){
            if(isPointInCombination(p,i)){
                setTris(sym,p);
                return true;
            }
        }
    }
    return false;
}

bool valueBetterPosition(const int adv[8],const int dsv[8],char sym){
    int dsvInd = getIndOfValue(dsv,1);
    if(dsvInd != -1){
        //Find the crucial points
        int p1[2] ,p2[2];
        if(!find2FreePoints(dsvInd,p1,p2))return false;
        //Trovare se uno dei due punti ci da vantaggio
        if(pointGrantValue(p1,adv,sym))return true;
        if(pointGrantValue(p2,adv,sym))return true;
        //ELIMINARE COMBINAZIONE CORRENTE E RICURSIONE
        int *tmp = new int[8];
        for(int i=0;i<8;i++)tmp[i] = dsv[i];
        tmp[dsvInd] = -1;
        return valueBetterPosition(adv,tmp,sym);
    }else{
        return false;
    }
}

void computerTurn(char sym='O'){ //Inteligenza artificiale
    //Analisi dati...
    int *advantages = new int[8];
    int *disvantages = new int[8];
    // search for every combinationWin
    int *tmp;
    for(int i=0;i<8;i++){
        tmp = valueOfCombination(combinationWin[i],sym);
        advantages[i] = tmp[0];disvantages[i] = tmp[1];
    }
    delete tmp;
    //Fine analisi dati...
    //Osservare una possibile posizione di vantaggio estremo del PC e in caso aumentare questo vantaggio
    if(blockOrWin(advantages,sym))return;
    //Fase di individuazione del vantaggio estremo avversario e in caso bloccare la sua possibile prossima mossa
    if(blockOrWin(disvantages,sym))return;
    //Trovare un punto di contemporaneo vantaggio personale e svantaggio dell'avversario
    if(valueBetterPosition(advantages,disvantages,sym))return;
    //punto random
    while(true){
        int row =rand()%3, col=rand()%3;
        if(tris[row][col]==NOT_SET){
            setTris(sym,row,col);
            return;
        }
    }
}
//---------------------------------------------------------------
//Fine inteligenza artificiale
//---------------------------------------------------------------

//Gestione del turno del giocatore

void playerTurn(char sym='X',string msg="-",string r="Inserire mossa:"){
    setTris(sym,inputCoordinates(r,msg));
}

//Verifica della vittoria perdita o pareggio

void verifyWin(string wins="Hai Vinto! :)",string lose="Hai Perso! :(",string parity="Pareggio! :|"){
    cout << endl << endl << "-> ";
    if(win){
        if(meWin)
            cout << wins;
        else
            cout << lose;
    }else{
        cout << parity;
    }
    cout << endl;
}

bool verifyTable(){return (onWin() || arrayFull>=9);}

void playWithComputer(){
    resetTris();
    while(true){
        printTris();
        playerTurn();
        if(verifyTable())break;
        computerTurn();
        if(verifyTable())break;
    }
    printTris();
    verifyWin();
    SysPause();
}

void playWithPlayer(){
    string p1Name="Player 1",p2Name="Player 2";
    
    bool c = true;
    //Menu delle scelte
    while(c){
        string v1[]={"Play!","Nome player 1: \""+p1Name+"\"","Nome player 2: \""+p2Name+"\"","Indietro"};
        switch(MenuCreate(v1,4,"\nScegli l'opzione desiderata...\n\n")){
            case 0:
                c=false;
                break;
            case 1:
                cout << "-> Inserire nome Player 1:\n-> ";
                getline(cin,p1Name);
                break;
            case 2:
                cout << "-> Inserire nome Player 2:\n-> ";
                getline(cin,p2Name);
                break;
            case 3:
                return;
        }
    }


    resetTris();
    while(true){
        printTris();
        playerTurn('X',p1Name);
        if(verifyTable())break;
        printTris();
        playerTurn('O',p2Name);
        if(verifyTable())break;
    }
    printTris();
    verifyWin(p1Name+" hai vinto! :)",p2Name+" hai vinto! :)");
    SysPause();
}

int main(){
    srand(time(NULL));
    //Menu delle scelte
    string v1[] ={"1) Single Player","2) 2 Players","3) Exit"};
    while(true){
        switch(MenuCreate(v1,3,"\nOXOXOXOXOXOXOXOXOXOXOXOXOXOXOXOXOXOX\nOXOXOXOXOX TERMINAL TRIS! XOXOXOXOXO\nOXOXOXOXOXOXOXOXOXOXOXOXOXOXOXOXOXOX\n\n")){
            case 0:
                playWithComputer();
                break;
            case 1:
                playWithPlayer();
                break;
            case 2:
                return 0;
        }
    }
}
