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

using namespace std;

struct node{
    vector<int> links;
};

void solve(int t) {
    int N, M, X, Y;
    cin >> N >> M;
    cin >> X >> Y;

    vector<node> nodes(N);
    vector<char> L(M);
    for (int i = 0; i < M; i++) {
        int a,b;
        cin >> a >> b >> L[i];
        nodes[a].links.push_back(b);
        nodes[b].links.push_back(a);
    }

    queue<int> to_visit,next;
    to_visit.push(X);
    vector<bool> visited(N,false);
    int risposta = 0;
    bool solved = false;
    while(!to_visit.empty() || !next.empty()){
        if(to_visit.empty()){
            swap(to_visit,next);
            risposta++;
        }
        int t = to_visit.front();
        to_visit.pop();
        if(t==Y){
            solved = true;
            break;
        }
        visited[t] = true;
        for(int l:nodes[t].links){
            if(!visited[l]){
                next.push(l);
            }
        }
    }
    

    if(!solved){risposta = -1;}
    
    

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