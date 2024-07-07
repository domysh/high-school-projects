#include <bits/stdc++.h>
using namespace std;

#ifdef EVAL
#define dbg(x) (x)
#else
#define dbg(x) (cerr << "[" << __FILE__ << ":" << __LINE__ << "] '" << #x << "' = '" << x << "'" << endl,x)
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

long long smaltisci(int N, int M, vector<int> A, vector<vector<int>> B) {
  return 42;
}
