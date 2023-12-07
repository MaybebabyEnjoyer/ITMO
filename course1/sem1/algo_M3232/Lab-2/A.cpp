#include <vector>
#include <iostream>

using namespace std;

int main() {
	int n;
	scanf("%d", &n);
	vector<long long> min;
	int size = -1;
	for (int i = 0; i < n; i++) {
		int operation;
		scanf("%d", &operation);
		if (operation == 1) {
			long long num;
			scanf("%lld", &num);
			if (size == -1) {
				min.push_back(num);
			} else if (num < min.at(size)) {
				min.push_back(num);
			} else {
				min.push_back(min.at(size));
			}
			size++;
		} else if (operation == 2) {
			min.pop_back();
			size--;
		} else if (operation == 3) {
			printf("%lld\n", min.at(size));
		}
	}
	return 0;
}
