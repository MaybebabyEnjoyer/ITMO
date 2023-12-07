#pragma GCC optimize("Ofast")

#include <iostream>

using namespace std;

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);

    int n;
    cin >> n;
    long long dp[10][n];
    for (int i = 0; i < 10; i++) {
        for (int j = 0; j < n; j++) {
            dp[i][j] = 0;
        }
    }
    for (int i = 0; i < 10; i++) {
        dp[i][0] = 1;
    }
    const long long mod = 1000000000;
    for (int i = 1; i < n; i++) {
        dp[0][i] = (dp[4][i - 1] + dp[6][i - 1]) % mod;
        dp[1][i] = (dp[6][i - 1] + dp[8][i - 1]) % mod;
        dp[2][i] = (dp[7][i - 1] + dp[9][i - 1]) % mod;
        dp[3][i] = (dp[4][i - 1] + dp[8][i - 1]) % mod;
        dp[4][i] = (dp[0][i - 1] + dp[3][i - 1] + dp[9][i - 1]) % mod;
        dp[6][i] = (dp[0][i - 1] + dp[1][i - 1] + dp[7][i - 1]) % mod;
        dp[7][i] = (dp[2][i - 1] + dp[6][i - 1]) % mod;
        dp[8][i] = (dp[1][i - 1] + dp[3][i - 1]) % mod;
        dp[9][i] = (dp[2][i - 1] + dp[4][i - 1]) % mod;
    }
    long long result = 0;
    for (int i = 0; i < 10; i++) {
        if (i != 0 && i != 8) {
            result = (result + dp[i][n - 1]) % mod;
        }
    }
    cout << result << endl;

    return 0;
}
