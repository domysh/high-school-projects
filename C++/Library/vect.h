#include <ostream>
#include <type_traits>
using namespace std;

template <class T>
struct vect{
    private:
    T* v = new bool[0];
    size_t length;
    public:
    vect(size_t LEN){
        v = new T[LEN];
        length = LEN;
    }

    template <typename N_type>
    T& operator[](N_type i){
        static_assert(is_arithmetic<N_type>::value, "NumericType must be numeric");
        if(v == nullptr) throw invalid_argument("Null Pointer");
        else if(i >= length || i<0) throw invalid_argument("Out of Bound");        
        return v[i];
    }


    bool operator<<(T ele){
        for(size_t ind=0;ind<length;ind++){
            if(operator[](ind) == ele) return true;
        }
        return false;
    }

    vect<T> operator+(vect <T> a){
        vect<T> res = vect<T>(length+a.size());
        size_t i;
        for(i=0;i<length;i++){
            res[i] = operator[](i);
        }
        size_t j=i;
        for(size_t i=0;i<a.size();i++){
            res[j++] = a[i];
        }
        return res;
    }

    vect<T> operator*(int n){
        if (n>=0){
            vect<T> res = vect<T>(n*length);
            size_t count = 0;
            for(size_t i=0;i<n;i++){
                for(size_t j=0;j<length;j++){
                    res[count++] = operator[](j);
                }
            }
            return res;
        }else{
            return vect<T>(0);
        }
    }

    void resize(size_t n){
        if(n>=0 && n != length){
            T* nV = new T[n];
            for(size_t i=0;i<length&&i<n;i++){
                nV[i] = v[i];
            }
            clear();
            v = nV;
            length = n;
        }
    }

    void trim(size_t pos,bool reverse=false){
        if(pos >= (length-1) || pos<0)return;
        if (reverse){
            size_t nsize = length - pos;
            T* nV = new T[nsize];
            for(size_t i=pos;i<length;i++){nV[i-pos] = v[i];}
            clear();
            v = nV;
            length = nsize;
        }else{
            resize(pos+1);
        }
    }

    void clear(){
        delete[] v;
        v = new T[0];
        length = 0;
    }

    size_t ind(T ele){
        for(size_t i=0;i<length;i++){
            if(operator[](i) == ele) return i;
        }
        return -1;
    }

    vect<T> copy(){
        vect<T> res = vect<T>(length);
        for(size_t i=0;i<length;i++){
            res[i] = operator[](i);
        }
        return res;
    }

    size_t size(){return length;}

};



template <class T>
string to_string(vect<T> v){
    string res = "[";
    for (size_t i=0;i<v.size();i++){
        res += to_string(v[i]);
        if (i != v.size()-1) res+= ", ";
    }
    res+="]";
    return res;
}

template <class T>
ostream& operator<<(ostream &chan,vect<T> v){
    chan << to_string(v);
    return chan;
}
