#include <algorithm>
#include <cctype>
#include <cmath>
#include <cstdio>
#include <queue>
#include <set>
#include <sstream>
#include <stack>
#include <string>
#include <vector>
#include <cstdlib>
#include <cstring>
#include <iostream>
#include <map>
#include <queue>
#pragma GCC optimize("O3,unroll-loops")
#pragma GCC target("avx2,bmi,bmi2,lzcnt,popcnt")

using namespace std;

int arr[100007];
int tree[21][100007*4];
int lazy[21][100007*4];

void build(int node, int l, int r, int idx) {
    if (l == r) {
        tree[idx][node] = 0;
        if (arr[l] & (1 << idx)) {
            tree[idx][node] = 1;
        }
        return;
    }

    int m = (l + r) >> 1;
    build(node * 2, l, m, idx);
    build(node * 2 + 1, m + 1, r, idx);

    tree[idx][node] = tree[idx][node * 2] + tree[idx][node * 2 + 1];
}

void update(int node, int l, int r, int idx, int i, int j) {
    if (lazy[idx][node]) {
        tree[idx][node] = (r - l + 1) - tree[idx][node];
        if (l != r) {
            lazy[idx][node * 2] ^= 1;
            lazy[idx][node * 2 + 1] ^= 1;
        }
        lazy[idx][node] = 0;
    }
    if (l > j || r < i) {
        return;
    }
    if (l >= i && r <= j) {
        tree[idx][node] = (r - l + 1) - tree[idx][node];
        if (l != r) {
            lazy[idx][node * 2] ^= 1;
            lazy[idx][node * 2 + 1] ^= 1;
        }
        lazy[idx][node] = 0;
        return;
    }

    int m = (l + r) / 2;
    update(node * 2, l, m, idx, i, j);
    update(node * 2 + 1, m + 1, r, idx, i, j);

    tree[idx][node] = tree[idx][node * 2] + tree[idx][node * 2 + 1];
}

int query(int node, int l, int r, int idx, int i, int j) {
    if (lazy[idx][node]) {
        tree[idx][node] = (r - l + 1) - tree[idx][node];
        if (l != r) {
            lazy[idx][node * 2] ^= 1;
            lazy[idx][node * 2 + 1] ^= 1;
        }
        lazy[idx][node] = 0;
    }
    if (l > j || r < i) {
        return 0;
    }
    if (l >= i && r <= j) {
        return tree[idx][node];
    }

    int m = (l + r) / 2;
    int p1 = query(node * 2, l, m, idx, i, j);
    int p2 = query(node * 2 + 1, m + 1, r, idx, i, j);

    return p1 + p2;
}

int main() {
    std::ios_base::sync_with_stdio(false);
    cin.tie(NULL);

    int n, m, l, r, x, c;

    scanf("%d", &n);

    for (int i = 0; i < n; i++) {
        scanf("%d", &arr[i]);
    }

    for (int i = 0; i < 20; i++) {
        build(1, 0, n - 1, i);
    }

    scanf("%d", &m);

    for (int i = 0; i < m; i++) {
        scanf("%d", &c);
        if (c == 1) {
            scanf("%d %d", &l, &r);
            long long ans = 0;
            for (int j = 0; j < 20; j++) {
                ans += (1LL << j) * query(1, 0, n - 1, j, l-1, r-1);
            }
            printf("%I64d\n", ans);
        }

        else {
            scanf("%d %d %d", &l, &r, &x);
            for (int j = 0; j < 20; j++) {
                if (x & (1 << j)) {
                    update(1, 0, n - 1, j, l-1, r-1);
                }
            }
        }
    }
}