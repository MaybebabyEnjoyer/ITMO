from functools import lru_cache


@lru_cache(maxsize=None)
def partition(n):
    if n == 0:
        return [[]]
    else:
        return [[n]] + [[i] + j
                        for i in range(1, n)
                        for j in partition(n - i)
                        if i <= j[0]]


if __name__ == '__main__':
    l = input()
    answers = sorted(partition(eval(l)))