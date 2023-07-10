def turtle_coins():
    N, M = map(int, input().split())
    a = [[int(x) for x in input().split()] for _ in range(N)]

    dp = [[0 for _ in range(M + 1)] for _ in range(N + 1)]
    path = [['' for _ in range(M + 1)] for _ in range(N + 1)]

    for i in range(1, N + 1):
        for j in range(1, M + 1):
            if i == 1 and j == 1:
                dp[i][j] = a[i - 1][j - 1]
                path[i][j] = ""
            elif i == 1:
                dp[i][j] = dp[i][j - 1] + a[i - 1][j - 1]
                path[i][j] = "R"
            elif j == 1:
                dp[i][j] = dp[i - 1][j] + a[i - 1][j - 1]
                path[i][j] = "D"
            else:
                if dp[i - 1][j] > dp[i][j - 1]:
                    dp[i][j] = dp[i - 1][j] + a[i - 1][j - 1]
                    path[i][j] = "D"
                else:
                    dp[i][j] = dp[i][j - 1] + a[i - 1][j - 1]
                    path[i][j] = "R"

    max_coins = dp[N][M]
    direction = ""
    i = N
    j = M
    while i > 1 or j > 1:
        direction = path[i][j] + direction
        if path[i][j] == "D":
            i -= 1
        else:
            j -= 1
    print(max_coins)
    print(direction)


turtle_coins()
 