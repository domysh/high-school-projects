#include <cstdio>
#include <iostream>
#include <vector>

using namespace std;

void brucia(int N, vector<int> &M, vector<int> &B, vector<long long> &T);

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(0);
    cout.tie(0);
    
	int n;
	cin >> n;
	
	vector <int> m(n), b(n);
	vector <long long> t(n, 0);
	
	for (int i = 0; i < n; i++)
		cin >> m[i] >> b[i];
	
	brucia(n, m, b, t);
	for (int i = 0; i < n; i++)
		cout << t[i] << " ";
	cout << "\n";
	
	return 0;	
}
