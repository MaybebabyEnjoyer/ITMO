from functools import lru_cache


@lru_cache(maxsize=None)
def get_correct_brackets_sequence_by_num(n: int, num: int) -> str:
	L = 50
	dp = [[0 for x in range(L)] for y in range(L)]
	dp[0][0] = 1
	for i in range(L - 1):
		for j in range(i + 1):
			if j + 1 < L:
				dp[i + 1][j + 1] += dp[i][j]
			if j - 1 >= 0:
				dp[i + 1][j - 1] += dp[i][j]
	result = ""
	bal = 0
	for i in range(2 * n):
		if dp[2 * n - (i + 1)][bal + 1] <= num:
			result += ")"
			num -= dp[2 * n - (i + 1)][bal + 1]
			bal -= 1
		else:
			result += "("
			bal += 1
	return result


if __name__ == '__main__':
	n, k = map(int, input().split())
	print(get_correct_brackets_sequence_by_num(n, k))
