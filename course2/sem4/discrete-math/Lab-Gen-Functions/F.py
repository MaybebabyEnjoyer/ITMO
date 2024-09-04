def main():
	n, m = map(int, input().split())
	MOD = 1000000007

	c = list(map(int, input().split()))
	was = [0] * (m + 1)
	dp = [[0] * 2 for _ in range(m + 1)]

	for i in range(n):
		was[c[i]] = 1

	dp[0][0] = 1
	dp[0][1] = 1

	for i in range(1, m + 1):
		dp[i][0] = 0
		dp[i][1] = 0
		for j in range(1, i + 1):
			dp[i][0] = (dp[i][0] + dp[i - j][1] * was[j]) % MOD
		print(dp[i][0], end=" ")
		for j in range(i + 1):
			dp[i][1] = (dp[i][1] + dp[j][0] * dp[i - j][0]) % MOD


if __name__ == "__main__":
	main()
