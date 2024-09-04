def combinations(n, k):
	if k == 0:
		return [[]]
	if n == 0:
		return []
	else:
		return [sorted([n] + combination) for combination in combinations(n - 1, k - 1)] + combinations(n - 1, k)


if __name__ == '__main__':
	n, k = map(int, input().split())
	answers = sorted(combinations(n, k))
	for answer in answers:
		print(*answer)
