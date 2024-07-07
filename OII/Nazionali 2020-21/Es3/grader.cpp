#include <iostream>
#include <string>

using namespace std;

int piega(int N, string S);

int main() {
    int N;
    string S;
    
    cin >> N >> S;

    cout << piega(N, S) << endl;

    return 0;
}
