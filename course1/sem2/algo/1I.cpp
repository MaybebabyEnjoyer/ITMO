#include <iostream>
#include <vector>

using namespace std;

struct Node {
    long long maxInRange;
    long long minInRange;

    Node(long long maxInRange = INT_MIN, long long minInRange = INT_MAX) : maxInRange(maxInRange), minInRange(minInRange) {}
};

Node combine(const Node &leftChild, const Node &rightChild) {
    return Node(max(leftChild.maxInRange, rightChild.maxInRange), min(leftChild.minInRange, rightChild.minInRange));
}

void build(vector<Node> &tree, const vector<long long> &a, int v, int l, int r) {
    if (l == r) {
        tree[v] = Node(a[l], a[l]);
    } else {
        int m = (l + r) >> 1;
        build(tree, a, v * 2 + 1, l, m);
        build(tree, a, v * 2 + 2, m + 1, r);
        tree[v] = combine(tree[v * 2 + 1], tree[v * 2 + 2]);
    }
}

Node get(const vector<Node> &tree, int v, int l, int r, int ql, int qr) {
    if (qr <= l || ql >= r) {
        return Node(INT_MIN, INT_MAX);
    }
    if (ql <= l && r <= qr) {
        return tree[v];
    }
    int m = (l + r) >> 1;
    Node leftChild = get(tree, 2 * v + 1, l, m, ql, qr);
    Node rightChild = get(tree, 2 * v + 2, m, r, ql, qr);
    return combine(leftChild, rightChild);
}

void add(vector<Node> &tree, int v, int l, int r, int pos, int val) {
    if (r - l == 1) {
        tree[v].maxInRange += val;
        tree[v].minInRange += val;
        return;
    }
    int m = (r + l) >> 1;
    if (pos < m) {
        add(tree, 2 * v + 1, l, m, pos, val);
    } else {
        add(tree, 2 * v + 2, m, r, pos, val);
    }
    tree[v] = combine(tree[2 * v + 1], tree[2 * v + 2]);
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    int pow = 1 << (32 - __builtin_clz(n - 1));
    vector<long long> array(pow);
    long long prev = 0;
    for (int i = 0; i < n; i++) {
        long long num;
        cin >> num;
        array[i] = num - (i == 0 ? 0 : prev);
        prev = num;
    }

    vector<Node> tree(4 * pow);
    build(tree, array, 0, 0, pow - 1);

    string ans;
    while (m > 0) {
        int type;
        cin >> type;
        if (type == 1) {
            int x, y;
            cin >> x >> y;
            bool flag = false;
            Node res;
            if (x < y) {
                res = get(tree, 0, 0, pow, x, y);
            } else {
                res = get(tree, 0, 0, pow, y, x);
                flag = true;
            }
            if (flag) {
                if (-res.minInRange <= 1) {
                    ans += "YES\n";
                } else {
                    ans += "NO\n";
                }
            } else {
                if (res.maxInRange <= 1) {
                    ans += "YES\n";
                } else {
                    ans += "NO\n";
                }
            }
        } else {
            int l, r, d;
            cin >> l >> r >> d;
            add(tree, 0, 0, pow, l - 1, d);
            add(tree, 0, 0, pow, r, -d);
        }
        m--;
    }

    cout << ans;

    return 0;
}