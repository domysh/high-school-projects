#include <bits/stdc++.h>
using namespace std;

int answ(int n){
    int res = 0;
    for(int sum=n/3;sum<n;sum++){
        for(int a=1;a<=sum/2;a++){
            if(gcd(a,sum-a)+sum == n){
                //cout << "A " << a << " B " << sum-a << " GCD " << gcd(a,sum-a) << endl;
                if (sum-a == a) res ++;
                else res += 2;
            }
        }
    }
    return res;

}

void evadi(int Q, vector<int>& N) {
    for(int quest=0;quest<Q;quest++){
        N[quest] = answ(N[quest]);
    }
}
