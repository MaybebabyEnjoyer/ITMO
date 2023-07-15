#include <cmath>
#include <iostream>

using namespace std;

int main() {
    int n, m;
    cin >> n >> m;

    int arr[n];
    cin >> arr[0];

    int lg[n + 1];
    lg[1] = 0;
    for (int i = 2; i <= n; i++) {
        lg[i] = lg[i / 2] + 1;
    }

    for (int i = 1; i < n; i++) {
        arr[i] = (23 * arr[i - 1] + 21563) % 16714589;
    }

    int log = (int) (log2(n) + 1);
    int table[n][log];
    for (int i = 0; i < n; i++) {
        table[i][0] = arr[i];
    }

    for (int j = 1; (1 << j) <= n; j++) {
        for (int i = 0; (i + (1 << j) - 1) < n; i++) {
            table[i][j] = min(table[i][j - 1], table[i + (1 << (j - 1))][j - 1]);
        }
    }

    int u, v;
    cin >> u >> v;

    int left = min(u, v);
    int right = max(u, v);
    int k = lg[abs(u - v) + 1];
    int ans = min(table[left - 1][k], table[right - (1 << k)][k]);

    int i = 1;
    while (i < m) {
        u = ((17 * u + 751 + ans + 2 * i) % n) + 1;
        v = ((13 * v + 593 + ans + 5 * i) % n) + 1;
        left = min(u, v);
        right = max(u, v);
        k = lg[abs(u - v) + 1];
        ans = min(table[left - 1][k], table[right - (1 << k)][k]);
        i++;
    }

    cout << u << " " << v << " " << ans << endl;

    return 0;
}