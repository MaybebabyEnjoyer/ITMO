#include <set>
#include <vector>
#include <iostream>

using namespace std;

vector<int> result;
vector<set<int>> color_set;

void dfs(int v, int colors[], vector<int> tree[]) {
    color_set[v].insert(colors[v]);
    for (int i = 0; i < tree[v].size(); i++) {
        int to = tree[v][i];
        dfs(to, colors, tree);
        if (color_set[v].size() < color_set[to].size()) {
            color_set[v].swap(color_set[to]);
        }
        for (int color : color_set[to]) {
            color_set[v].insert(color);
        }
    }
    result[v] = (int)color_set[v].size();
}

int main() {
    int n;
    scanf("%i", &n);
    int colors[n + 1];
    vector<int>tree[n + 1];
    for (int i = 1; i < n + 1; i++) {
        int p, c;
        scanf("%i %i", &p, &c);
        tree[p].push_back(i);
        colors[i] = c;
    }
    result.resize(n + 1);
    color_set.resize(n + 1);
    dfs(0, colors, tree);
    for (int i = 1; i < n + 1; i++) {
        printf("%i ", result[i]);
    }
    return 0;
}
