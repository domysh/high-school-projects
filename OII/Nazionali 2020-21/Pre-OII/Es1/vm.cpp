#include <vector>
using namespace std;

long long aggiorna(int N, vector<int> A, vector<int> B) {
    
    long long res = 0;
    for(int i=N-2;i>=0;i--){
        int updates = (A[i+1]-A[i])/B[i];
        res+=updates;
        A[i]+=updates*B[i];
    }
    return res;
}
