#pragma GCC optimize("Ofast")

#include <iostream>

using namespace std;

struct Node {
    long long sum;
    long long value;
    long long prior;
    Node *left = nullptr;
    Node *right = nullptr;

    Node(const long long x, const long long random) {
        sum = x;
        value = x;
        prior = random;
    }
};

struct Tree {
private:
    Node *root = nullptr;

    static void update(Node *current) {
        long long left_sum = current->left ? current->left->sum : 0;
        long long right_sum = current->right ? current->right->sum : 0;
        current->sum = left_sum + right_sum + current->value;
    }

    pair<Node *, Node *> split(Node *current, long long k) {
        if (current == nullptr) {
            return make_pair(nullptr, nullptr);
        }
        if (current->value < k) {
            pair<Node *, Node *> tuple = split(current->right, k);
            current->right = tuple.first;
            update(current);
            return make_pair(current, tuple.second);
        } else {
            pair<Node *, Node *> tuple = split(current->left, k);
            current->left = tuple.second;
            update(current);
            return make_pair(tuple.first, current);
        }
    }

    Node *merge(Node *node1, Node *node2) {
        if (node1 == nullptr) {
            return node2;
        }
        if (node2 == nullptr) {
            return node1;
        }
        if (node1->prior > node2->prior) {
            node1->right = merge(node1->right, node2);
            update(node1);
            return node1;
        } else {
            node2->left = merge(node1, node2->left);
            update(node2);
            return node2;
        }
    }

    Node *exists(Node *current, long long x) {
        if (current == nullptr) {
            return nullptr;
        }
        if (current->value == x) {
            return current;
        }
        if (current->value < x) {
            return exists(current->right, x);
        } else {
            return exists(current->left, x);
        }
    }

    Node *insert(Node *current, Node *value) {
        pair<Node *, Node *> tuple = split(current, value->value);
        tuple.first = merge(tuple.first, value);
        return merge(tuple.first, tuple.second);
    }

    Node *leftMax(Node *current, long long k) {
        if (current == nullptr) {
            return nullptr;
        }
        if (k < current->value) {
            Node *result = leftMax(current->left, k);
            return result != nullptr ? result : current;
        }
        return leftMax(current->right, k);
    }

    Node *rightMin(Node *current, long long k) {
        if (current == nullptr) {
            return nullptr;
        }
        if (current->value < k) {
            Node *result = rightMin(current->right, k);
            return result != nullptr ? result : current;
        }
        return rightMin(current->left, k);
    }

    void toString(Node *current) {
        if (current == nullptr) {
            return;
        }
        toString(current->left);
        cout << current->value << " ";
        toString(current->right);
    }

public:
    bool exists(long long x) {
        return exists(root, x);
    }

    void insert(long long x) {
        root = insert(root, new Node(x, rand() * RAND_MAX));
    }

    long long sum(int l, int r) {
        Node *left = leftMax(root, l - 1);
        if (left == nullptr) {
            return 0;
        }
        Node *right = rightMin(root, r + 1);
        if (right == nullptr) {
            return 0;
        }
        if (right->value < left->value) {
            return 0;
        }
        pair<Node *, Node *> node1 = split(root, left->value);
        pair<Node *, Node *> node2 = split(node1.second, right->value + 1);
        const long long result = node2.first->sum;
        Node *temp = merge(node2.first, node2.second);
        root = merge(node1.first, temp);
        return result;
    }

    void toString() {
        toString(root);
        cout << endl;
    }
};

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);

    int n;
    cin >> n;
    Tree tree;
    bool last;
    long long prev = 0;
    for (int i = 0; i < n; i++) {
        char operation;
        cin >> operation;
        switch (operation) {
            case '+': {
                long long x;
                cin >> x;
                x = last ? (x + prev) % 1000000000 : x;
                if (!tree.exists(x)) {
                    tree.insert(x);
                }
                last = false;
                break;
            }
            case '?': {
                int l, r;
                cin >> l >> r;
                prev = tree.sum(l, r);
                cout << prev << endl;
                last = true;
                break;
            }
            default: {
                cerr << "Unsupported operation!" << endl;
                break;
            }
        }
    }

    return 0;
}
