#include <iostream>
#include <vector>
#pragma GCC optimize("O3,unroll-loops")
#pragma GCC target("avx2,bmi,bmi2,lzcnt,popcnt")

const int s = 1e6 + 1;
int tree[s];

void update(int p) {
    while (p <= 1e6) {
        tree[p]++;
        p += p & -p;
    }
}

int get(int p) {
    int res = 0;
    while (p > 0) {
        res += tree[p];
        p -= p & -p;
    }
    return res;
}

int main() {
    std::ios_base::sync_with_stdio(false);
    std::cin.tie(NULL);

    int n;
    std::cin >> n;

    std::vector<int> c(n + 1);
    std::vector<std::pair<int, int>> a(n + 1);
    for (int i = 1; i <= n; i++) {
        a[i].second = i;
        std::cin >> a[i].first;
    }
    a[0].first = 0;
    std::sort(a.begin(), a.end());
    for (int i = 1; i <= n; i++) {
        c[a[i].second] = i;
    }

    long long ans;
    ans = tree[0] = 0;
    for (int i = 1; i <= n; i++) {
        long long m = i - 1 - get(c[i]);
        long long l = c[i] + m - i;

        ans += (l * m);
        update(c[i]);
    }
    std::cout << ans << '\n';
    return 0;
}