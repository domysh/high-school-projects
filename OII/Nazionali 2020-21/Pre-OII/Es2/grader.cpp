#include <iostream>
#include <vector>
using namespace std;

long long conta(int N, vector<int> A);

int main() {

    int N;
    cin >> N;
    vector<int> A(N);
    for (int& i : A) {
        cin >> i;
    }

    cout << conta(N, A);

    cout << endl;

    return 0;
}
