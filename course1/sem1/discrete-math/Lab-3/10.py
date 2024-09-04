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
    n = int(input())
    answers = map(lambda x: '+'.join(map(str, x)), partition(n))
    for answer in sorted(set(answers)):
        print(answer)
