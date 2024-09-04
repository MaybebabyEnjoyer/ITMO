from typing import List


def is_reflect(arr) -> bool:
    for i in range(len(arr)):
        if arr[i][i] != 1:
            return False
    return True


def is_antireflexive(arr) -> bool:
    for i in range(len(arr)):
        if arr[i][i] == 1:
            return False
    return True


def is_symmetric(arr) -> bool:
    for i in range(len(arr)):
        for j in range(len(arr)):
            if not (not arr[i][j] or arr[j][i]):
                return False
    return True


def is_transitive(arr) -> bool:
    for i in range(len(arr)):
        for j in range(len(arr)):
                for k in range(len(arr)):
                    if not(not (arr[i][j] == 1 and arr[j][k] == 1) or arr[i][k] == 1):
                        return False
    return True


def is_antisymmetric(arr) -> bool:
    for i in range(len(arr)):
        for j in range(len(arr[i])):
            if not (not (arr[i][j] == 1 and arr[j][i] == 1) or i == j):
                return False
    return True


def is_equivalence(arr) -> bool:
    return is_reflect(arr) and is_symmetric(arr) and is_transitive(arr)


def is_partial_order(arr) -> bool:
    return is_reflect(arr) and is_antisymmetric(arr) and is_transitive(arr)


def composition(arr1, arr2) -> List[List[int]]:
    arr = [[0 for _ in range(len(arr1))] for _ in range(len(arr1))]
    for i in range(len(arr1)):
        for j in range(len(arr1)):
            for k in range(len(arr1)):
                if arr1[i][j] == 1 and arr2[j][k] == 1:
                    arr[i][k] = 1
    return arr


if __name__ == '__main__':
    n = int(input())
    arr1 = [list(map(int, input().split())) for _ in range(n)]
    arr2 = [list(map(int, input().split())) for _ in range(n)]
    functions = [is_reflect, is_antireflexive, is_symmetric, is_antisymmetric, is_transitive]
    ans1, ans2 = "", ""
    for func in functions:
        ans1 += str(int(func(arr1))) + " "
        ans2 += str(int(func(arr2))) + " "

    print(ans1)
    print(ans2)
    comp_ans = composition(arr1, arr2)
    for i in range(len(comp_ans)):
        print(*comp_ans[i])
