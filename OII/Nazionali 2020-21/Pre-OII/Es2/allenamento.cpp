#include <bits/stdc++.h>
using namespace std;

set<pair<int,int>> used;
vector<pair<int,int>> *values;

int getx(int l, int r){
    for(int i=0;i<values->size();i++){
        if(values->at(i).second>=l && values->at(i).second<=r){
            return values->at(i).second;
        }
    }
    exit(-1);
}

int gety(int l, int r){
    for(int i=values->size()-1;i>=0;i--){
        if(values->at(i).second>=l && values->at(i).second<=r){
            return values->at(i).second;
        }
    }
    exit(-1);
}

long long solve(int l, int r,int x,int y, vector<int> const &A){
    if (r-l<=0) return 0;

    if (x == -1) x = getx(l,r);
    if (y == -1) y = gety(l,r);

    //cout << "POS " << l+1 << " " << r+1 << " XY " << x << " " << y << endl; 

    pair<int,int> xy = make_pair(x,y);
    if (used.find(xy) != used.end()) return 0;
    used.insert(xy);

    if(x < y){
        //cout << " OK -> " << (x-l+1)*(r-y+1) << endl; 

        return (x-l+1)*(r-y+1)
                +solve(l,y-1,x,-1,A)
                +solve(x+1,r,-1,y,A);
    }else{
        //cout << " NO -> " << 0 << endl; 

        return solve(l,x-1,-1,y,A) + solve(y+1,r,x,-1,A);
    }
}


long long conta(int N, vector<int> A) {
    vector<pair<int,int>> values_(N);
    for(int i=0;i<N;i++)values_[i] = make_pair(A[i],i);
    sort(values_.begin(),values_.end());
    //for(int i=0;i<N;i++)
    //    cout << values_[i].first << "," << values_[i].second << " - ";
    //cout << endl;
    values = &values_;
    return solve(0, N-1, -1, -1, A);
}

