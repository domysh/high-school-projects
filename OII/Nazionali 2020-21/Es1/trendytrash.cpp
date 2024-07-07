#include <bits/stdc++.h>
using namespace std;

#ifdef EVAL
#define dbg(x) (x)
#else
#define dbg(x) (cerr << "[" << __FILE__ << ":" << __LINE__ << "] '" << #x << "' = '" << x << "'" << endl,x)
#endif

int N,M;

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

#define deleted 2000

int match_first(int dim,vector<int>& v){
    for(int i =0;i<v.size();i++){
        if (abs(v[i]) == dim){
            return i;
        }
    }
    return -1;
}

void change_status_col(int find, int &counter, vector<int> &rows, vector<int> &cols){
    counter--;
    int sum = (cols[find]>0)?-1:1;
    cols[find] = deleted;
    for(int i=0;i<N;i++){rows[i]+=sum;}
}

void change_status_row(int find, int &counter, vector<int> &rows, vector<int> &cols){
    counter--;
    int sum = (rows[find]>0)?-1:+1;
    rows[find] = deleted;
    for(int i=0;i<M;i++){cols[i]+=sum;}
}

int pulisci(int n, int m, vector<string> S) {
    N=n;M=m;
  // if 1 == 1
  // if 0 == -1
  vector<int> rows(N);
  vector<int> cols(M);
  for(int i=0;i<N;i++){
    for(int j=0;j<M;j++){
      if(S[i][j] == '1'){
        rows[i] +=1;
        cols[j] +=1;
      }else{
        rows[i] -=1;
        cols[j] -=1;
      }
    }
  }
  int e_col = M, e_row = N, col_find=-1, row_find=-1;
  while(e_col != 0 && e_row != 0){
      dbg(rows);dbg(cols);
      dbg(e_row);dbg(e_col);
    row_find = dbg(match_first(e_col,rows));
    col_find = dbg(match_first(e_row,cols));
    if(row_find == -1 && col_find == -1){
        break;
    }else if(row_find != -1 && col_find != -1){
        if (e_row > e_col){
            change_status_row(row_find,e_row,rows,cols);
        }else{
            change_status_col(col_find,e_col,rows,cols);
        }
    }else{
        if(row_find != -1){
            change_status_row(row_find,e_row,rows,cols);
        }else{
            change_status_col(col_find,e_col,rows,cols);
        }
    }
    dbg("--------------");
  }
  return e_col*e_row;
  
}
