from math import factorial
from functools import lru_cache


@lru_cache(maxsize=None)
def get_permutation(n: int, k: int) -> list:
    p = [0] * n
    used = [False] * (n + 1)
    for i in range(n):
        for j in range(1, n + 1):
            if not used[j]:
                if factorial(n - i - 1) < k:
                    k -= factorial(n - i - 1)
                else:
                    p[i] = j
                    used[j] = True
                    break
    return p


if __name__ == '__main__':
    n, k = map(int, input().split())
    print(" ".join(map(str, get_permutation(n, k + 1))))
