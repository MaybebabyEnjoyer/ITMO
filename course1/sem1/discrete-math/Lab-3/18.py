from functools import lru_cache


@lru_cache(maxsize=None)
def get_position_of_correct_brackets_sequence(s: str) -> int:
	dp = [[0 for x in range(50)] for y in range(50)]
	dp[0][0] = 1
	for i in range(49):
		for j in range(i + 1):
			if j + 1 < 50:
				dp[i + 1][j + 1] += dp[i][j]
			if j - 1 >= 0:
				dp[i + 1][j - 1] += dp[i][j]
	n = len(s)
	bal = 0
	result = 0
	for i in range(n - 1):
		if s[i] == "(":
			bal += 1
		else:
			result += dp[n - (i + 1)][bal + 1]
			bal -= 1
	return result


if __name__ == '__main__':
	print(get_position_of_correct_brackets_sequence(input()))
