#include <bits/stdc++.h>

using namespace std;

bool chiedi(int);
void stato();
void sposta();

//#define PRINTS

int mode = 1;
int pos = 0;
int N;
bitset<1000> status[1000];
bitset<1000> bits;

void prints(){
    cout << "-------------" << endl;
    cout << "POS " << pos << " MODE " << mode << " N " << N << endl;
    for(int i=0;i<N;i++){
        for(int j=0;j<N;j++)
            cout << status[i][j];
        cout << endl;
    }
    cout << "-------------" << endl;    
}

void sw(){
    #ifdef PRINTS
    cout << "SWITCH!" << endl;
    #endif
    stato();
    if (mode == 1) mode = -1;
    else mode = 1;
}

void setmode(int m){
    if (mode != m) sw();
}

void moveto(int to){
    if (to>pos) setmode(1);
    else if (to<pos) setmode(-1);
    else return;
    for(int i=0; i<abs(to-pos); i++) sposta();
    pos = to;
}

void ask(int n){
    bits.set();
    if(chiedi(n)) bits >>= (1000-(pos+1));
    else bits <<= (pos+1);
    status[n] &= bits;
}

void full_ask(){
    for(int i=0;i<N;i++)
        if(status[i][pos])
            ask(i);
}


void fill_status(int a, int b){
    #ifdef PRINTS
    prints();
    #endif
    if (a>=b) return;
    if (a == b-1){
        full_ask();
    }else{
        int break_point = a+(b-a)/2;
        #ifdef PRINTS
        cout << "MOVETO " << break_point-1 << endl;
        #endif
        moveto(break_point-1);
        full_ask();
        if (mode == 1){
            fill_status(break_point,b);
            fill_status(a,break_point);
        }else{
            fill_status(a,break_point);
            fill_status(break_point,b);
        }
    }
}
void fill_status_B_optimization(int a, int b){
    #ifdef PRINTS
    prints();
    #endif
    if (a>=b) return;
    if (a == b-1){
        full_ask();
    }else{
        int stat = mode;
        int break_point = a+(b-a)/2;
        if (stat == -1)
            fill_status_B_optimization(break_point,b);
        #ifdef PRINTS
        cout << "MOVETO " << break_point-1 << endl;
        #endif
        moveto(break_point-1);
        full_ask();
        if (stat == 1)
            fill_status_B_optimization(break_point,b);
        fill_status_B_optimization(a,break_point);
    }
}

void solve(int H[]){
    for(int i=0;i<N;i++)
        for(int j=0;j<N;j++)
            if(status[i][j])
                H[j] = i;
}

void indovina(int n, int A, int B, int C, int H[]) {
    N = n;
    for(int i=0;i<N;i++) status[i].set();
    if (B<20){
        fill_status_B_optimization(0,N);
    }else{
        fill_status(0,N);
    }
    solve(H);
}

