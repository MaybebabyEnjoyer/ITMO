from math import factorial


def permute(start: int, need: int, get: list, old: list, d: int) -> int:
    if start == need:
        return d
    used = 0
    for i in range(1, need + 1):
        if not old[i]:
            used += 1
            if get[start] == i:
                old[i] = True
                return permute(start + 1, need, get, old, d)
            d += factorial(need - start - 1)


if __name__ == '__main__':
    n = int(input())
    p = [int(x) for x in input().split()]
    no_need = [False] * (n + 1)
    print(permute(0, n, p, no_need, 0))
