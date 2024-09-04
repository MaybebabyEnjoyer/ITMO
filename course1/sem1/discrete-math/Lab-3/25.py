def get_next_combination(n: int, k: int, c: list):
	c.append(n + 1)
	i = k - 1
	while i >= 0 and (c[i + 1] - c[i] < 2):
		i -= 1
	if i >= 0:
		c[i] += 1
		for j in range(i + 1, k):
			c[j] = c[j - 1] + 1
		c.pop()
		return c
	return -1


if __name__ == '__main__':
	n, k = map(int, input().split())
	c = [int(x) for x in input().split()]
	result = get_next_combination(n, k, c)
	if result == -1:
		print(-1)
	else:
		print(*result)
