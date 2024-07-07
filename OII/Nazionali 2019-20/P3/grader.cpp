#include <stdio.h>
#include <iostream>

using namespace std;

//#define DBG

static int click = 0;
static long long int emoji = 0;
static long long int clipboard = 0;

void auguri(long long int N);

void aggiungi() {  
    click += 1;
    emoji += 1;
    #ifdef DBG
    cout << "Aggiungi! " << emoji <<  endl;
    #endif
}

void copia() {
    click += 2;
    clipboard = emoji;
    #ifdef DBG
    cout << "Copia! " << clipboard << endl;
    #endif
}

void incolla() {
    if (clipboard == 0) {
        fprintf(stderr, "Attenzione: non ha senso chiamare 'incolla' senza aver prima chiamato 'copia'.\n");
    }
    click += 1;
    emoji += clipboard;
    #ifdef DBG
    cout << "Incolla! " << emoji <<  endl;
    #endif
}

int main() {
    long long int N;
    scanf("%lld", &N);

    auguri(N);

    if (emoji != N) {
        fprintf(stderr, "Attenzione: il messaggio costruito ha %lld emoji invece che %lld.\n", emoji, N);
    }

    printf("%d\n", click);
}