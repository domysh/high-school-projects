#include <bits/stdc++.h>
using namespace std;

#ifdef EVAL
#define dbg(x) (x)
#else
#define dbg(x) (cerr << "[" << __FILE__ << ":" << __LINE__ << "] '" << #x << "' = '" << x << "'" << endl,x)
#endif

#define int_bits 8*4

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

struct camion{
  int id;
  int t;
  int match;
  camion (int _id,int _t,int _m):id(_id),t(_t),match(_m){};
};

bool operator>(const camion a, const camion b){
  return (a.t < b.t)?true:( (b.t < a.t)?false:a.match < b.match );
}
bool operator<(const camion a, const camion b){
  return !(a > b);
}

int calc_match(int t, int m){
  bitset<int_bits> test;
  test.set();
  test = ~(test << m);
  test &= t;
  int res = 0;
  for(int i=0;i<m;i++)
    if (test[i])
      res++;
  return res;
}

long long riciclo(int N, int M, vector<int> T, vector<int> P) {
  priority_queue<camion> transport_prior;
  for(int i=0;i<N;i++){
    transport_prior.push(camion(i,T.at(i),calc_match(T.at(i),M)));
  }
  int type_sel = 0;
  long long res = 0;
  int cost = 1;
  while(!transport_prior.empty() && type_sel < M){
    camion cm = transport_prior.top();
    transport_prior.pop();
    while(true){
      int insertable = cm.t/cost;
      if(insertable == 0) break;
      if(insertable > P.at(type_sel)) insertable = P.at(type_sel);
      if(insertable > 0){
        cm.t -= insertable*cost;
        P.at(type_sel)-=insertable;
        res+=insertable;
      }else{
        type_sel++;
        if(type_sel == M) break;
        cost = pow(2,type_sel);
      }
    }
  }
  return res;
}
