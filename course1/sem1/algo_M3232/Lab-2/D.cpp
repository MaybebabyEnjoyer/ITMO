#pragma GCC optimize("Ofast")

#include <list>
#include <iostream>

using namespace std;

struct Queue {
private:
    list<int> list1, list2;

    void equalize_vec1_vec2() {
        if (list1.size() < list2.size()) {
            list1.push_back(list2.front());
            list2.pop_front();
        }
    }

public:
    void push_back(int x) {
        list2.push_back(x);
        equalize_vec1_vec2();
    }

    void push_middle(int x) {
        list2.push_front(x);
        equalize_vec1_vec2();
    }

    int pop_front() {
        const int result = list1.front();
        list1.pop_front();
        equalize_vec1_vec2();
        return result;
    }
};

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);

    Queue queue;

    int n;
    cin >> n;

    for (int i = 0; i < n; i++) {
        char operation;
        cin >> operation;
        switch (operation) {
            case '+' : {
                int x;
                cin >> x;
                queue.push_back(x);
                break;
            }
            case '*' : {
                int x;
                cin >> x;
                queue.push_middle(x);
                break;
            }
            case '-' : {
                cout << queue.pop_front() << endl;
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
