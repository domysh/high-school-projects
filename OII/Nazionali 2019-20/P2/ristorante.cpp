#include <vector>
#include <set>
#include <climits>
using namespace std;

int max_v(vector<int> v){
    int MAX = INT_MIN;
    for(int i=0;i<v.size();i++){
        if(v[i]>MAX) MAX = v[i];
    }
    return MAX;
}

int conta(int N, vector<int> &A, vector<int> &P, vector<int> &D) {
    
}

