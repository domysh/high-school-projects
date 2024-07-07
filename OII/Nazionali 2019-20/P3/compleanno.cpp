#include <iostream>
#include <stdlib.h>
#include <climits>

using namespace std;

//#define DBG

void aggiungi();
void copia();
void incolla();

int[] val = {2,3,4,5,7,11,13,17,19};

void f(long long int x){
	long long min = LONG_LONG_MAX;
	int ind_val;
	for(int i=0;i<9;i++){
		long long tmp = f(x/val[i]);
		if (tmp < min) min = tmp;
	}
	min+val[]
}

void auguri(long long int N) {
	if(N<=5){
		for(int i=0;i<N;i++){
			aggiungi();
		}
		return;
	}
	long long int tmp = 0;
	get_num(N,tmp,N);
}
