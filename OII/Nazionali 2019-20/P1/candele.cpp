#include <bits/stdc++.h>
using namespace std;

typedef struct{
	int M;
	int id;
	int B;
}candela;

bool compare(candela a, candela b){return a.M<b.M;}

int low_bound(vector<candela> &V,int T){
	int L = 0;
	int R = V.size();
	while (L<=R){
		int MID = ((R-L)/2)+L;
		if(V[MID].M < T){
			L = MID+1;
		}else{
			R = MID-1;
		}
	}
	return L;

}

void brucia(int N, vector<int> &M, vector<int> &B, vector<long long> &T) {
	vector<int> ON = vector<int>(N);
	vector<candela> data = vector<candela>(N);
	for(int i=0;i<N;i++){
		T[i] = -1;
		data[i].id = i;
		data[i].M = M[i];
		data[i].B = B[i];
	}
	T[0] = 0;
	int s_on = 0,len_on = 1;
	sort(data.begin(),data.end(),compare);
	while ((len_on-s_on)>0){
		int max = M[ON[s_on]];
		int min = B[ON[s_on]];
		if(max<min) swap(max,min);
		int c = low_bound(data, min);
		for(;data[c].M<=max && c<data.size();c++){
			int i = data[c].id;
			if(i == ON[s_on])continue;
			long long new_time = T[ON[s_on]]+abs(M[ON[s_on]]-M[i]);
			if(T[i] == -1 || T[i]>new_time){
				if(T[i]==-1){
					ON[len_on] = i;
					len_on++;
				}

				T[i] = new_time;
			}
		}
		s_on++;
		
	}
}
