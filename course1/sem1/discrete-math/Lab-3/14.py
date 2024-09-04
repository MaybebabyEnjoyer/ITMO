def permutations(n):
    if n == 0:
        return [[]]
    else:
        return [perm[:i] + [n] + perm[i:]
                for perm in permutations(n - 1)
                for i in range(n)]


if __name__ == '__main__':
    n = int(input())
    perm_to_find = [int(i) for i in input().split()]
    answers = sorted(permutations(n))
    print(answers.index(perm_to_find))
