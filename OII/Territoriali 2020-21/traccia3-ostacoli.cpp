#include <cstdio>
#include <cstring>
#include <algorithm>
#include <fstream>
#include <iostream>
#include <numeric>
#include <set>
#include <utility>
#include <vector>
#include <bits/stdc++.h>

using namespace std;
int N, L, D;

struct obs{
    int x,p,s;
};

bool sortObs(const obs& a, 
               const obs& b)
{
    return (a.s < b.s);
}

void solution(int time,int pos,int &res,vector<obs> const &OB){
    if(time > D) return;
    int next_value = res;
    for(int i=0;i<OB.size();i++){
        obs ele = OB[i];
        int dist = abs(pos-ele.x);
        if(time == ele.s) continue;
        if(dist+time <= ele.s){

            int tmp = res+ele.p;
            solution(ele.s,ele.x,tmp,OB);
            if(tmp > next_value) next_value = tmp;
        }     
    }
    res = next_value;
}

void solve(int t) {
    
    cin >> N >> L >> D;

    vector<obs> OB(N);
    for (int i = 0; i < N; i++) {
        int x,p,s;
        cin >> x >> p >> s;
        OB[i] = {x,p,s};
    }
    //sort(OB.begin(),OB.end(),sortObs);

    // aggiungi codice...
    int risposta = 0;
    solution(0,0,risposta,OB);
    cout << "Case #" << t << ": " << risposta << endl;
}

int main() {
    // se preferisci leggere e scrivere da file
    // ti basta decommentare le seguenti due righe:

    freopen("input.txt", "r", stdin);
    freopen("output.txt", "w", stdout);

    int T;
    cin >> T;

    for (int t = 1; t <= T; t++) {
        solve(t);
    }

    return 0;
}