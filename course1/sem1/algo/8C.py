def min_banknotes(denominations, S):
    n = len(denominations)
    dp = [float("inf")] * (S+1)
    dp[0] = 0
    banknotes = [-1] * (S + 1)
    for x in denominations:
        for i in range(x, S+1):
            if dp[i-x] + 1 < dp[i]:
                dp[i] = dp[i-x] + 1
                banknotes[i] = x
    if dp[S] == float("inf"):
        print(-1)
    else:
        representation = []
        i = S
        while i > 0:
            representation.append(banknotes[i])
            i -= banknotes[i]
        representation.sort(reverse=True)
        print(dp[S])
        for x in representation:
            print(x, end=" ")


n = int(input())
denominations = list(map(int, input().split()))
S = int(input())
min_banknotes(denominations, S)