n, k = map(int, input().split())

stirling = [[0 for x in range(n + 1)] for y in range(n + 1)]

for i in range(1, n + 1):
    for j in range(1, n + 1):
        if j > i:
            stirling[i][j] = 0
        elif i == j:
            stirling[i][j] = 1
        else:
            stirling[i][j] = j * stirling[i - 1][j] + stirling[i - 1][j - 1]


def check_build3(build):
    for i in range(k):
        if len(build[i]) == 0:
            return False
    for i in range(k - 1):
        if build[i][0] > build[i + 1][0]:
            return False
    return True


def gen3(cur_digit, build):
    if cur_digit == n:
        if check_build3(build):
            for row in build:
                for item in row:
                    print(item, end=' ')
                print()
            print()
        return
    for i in range(k):
        nb = [[item for item in row] for row in build]
        nb[i].append(cur_digit + 1)
        gen3(cur_digit + 1, nb)
        if len(build[i]) == 0:
            return


gen3(0, [[] for x in range(k)])
