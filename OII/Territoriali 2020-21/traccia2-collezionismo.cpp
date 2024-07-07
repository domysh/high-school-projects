#include <cstdio>
#include <cstring>
#include <algorithm>
#include <fstream>
#include <iostream>
#include <numeric>
#include <set>
#include <utility>
#include <vector>
#include <queue>
#include <cmath>

using namespace std;

void solve(int t) {
    int N, K;
    cin >> N >> K;

    vector<int> C(N);
    int risposta = 0;
    for (int i = 0; i < N; i++) {
        cin >> C[i];
    }
    sort(C.begin(),C.end());
    if (K == 1){
        risposta = abs(C[0]-C[C.size()-1]);
    }else{
        priority_queue<pair<int,int>> pq;

        for(int i=0;i<N;i++){
            if(i == N-1){
                pq.push({0,i});
            }else{
                pq.push({abs(C[i]-C[i+1]),i});
            }
        }
        vector<int> break_points(K-1);
        for(int i=0;i<K-1;i++){
            break_points[i] = pq.top().second;
            pq.pop();
        }
        sort(break_points.begin(),break_points.end());
        int last_point = 0;
        for(int i=0;i<K-1;i++){
            risposta += abs(C[last_point]-C[break_points[i]]);
            last_point = break_points[i]+1;
        }
        risposta += abs(C[last_point]-C[C.size()-1]);

    }



    // aggiungi codice...
    

    cout << "Case #" << t << ": " << risposta << "\n";
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

/*
42 = 0
4 0 2 = 4
23 21 = 2
42 23 21 4 2 0

0 2 4 21 23 42
2 2 17 2 19 0

1 3 7 9
2 4 2 0

*/