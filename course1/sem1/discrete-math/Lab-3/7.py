def permutations(n):
    if n == 0:
        return [[]]
    else:
        return [perm[:i] + [n] + perm[i:]
                for perm in permutations(n - 1)
                for i in range(n)]


if __name__ == '__main__':
    n = int(input())
    answers = sorted(permutations(n))
    for answer in answers:
        print(*answer)
