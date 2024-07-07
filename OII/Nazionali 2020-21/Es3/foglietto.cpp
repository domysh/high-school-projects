#include <bits/stdc++.h>
using namespace std;

#ifdef EVAL
#define dbg(x) (x)
#else
#define dbg(x) (cerr << "[" << __FILE__ << ":" << __LINE__ << "] '" << #x << "' = '" << (x) << "'" << endl,x)
#endif

template <typename T>
ostream& operator<<(ostream& o, const vector<T> v){
  o << "[";
  for(int i=0;i<v.size();i++){
    o << v[i];
    if(i != v.size()-1){
      o << ", ";
    }
  }
  o << "]";
  return o;
}

template <typename T, typename F>
ostream& operator<<(ostream& o, const pair<T,F> v){
    o << "(" << v.first << ", " << v.second << ")";
  return o;
}

int score_selection(int id, string const &S, int l, int r){
  int res = 0;
  for(int bef=r-1,nex=id+1;bef>=l && nex<r;bef--,nex++){
    if(S[bef] != S[nex]) res++;
    else return -1;
  }
  return res;
}

int piega(int N, string S) {
    int res = 0;
    int l=0,r=N;
    while(r-l > 1){
        pair<int,int> mx = make_pair(-1,-1);
        for(int i=l;i<r;i++){
            int res = score_selection(i,S,l,r);
            if (res>mx.first){
                mx.first = res;
                mx.second = i;
            }
        }
        auto [score,id] = mx;
        if(id-l-1<r-id-1){
            l=id;
        }else{
            r = id;
        }
        res++;
        dbg(mx);
        dbg(S);
    }
    if (S == "1") res++;
    return res;

}
