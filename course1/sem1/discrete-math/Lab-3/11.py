def subsets(n):
	if n == 0:
		return [[]]
	else:
		return subsets(n - 1) + [sorted([n] + subset) for subset in subsets(n - 1)]


if __name__ == '__main__':
	n = int(input())
	answers = sorted(subsets(n))
	for answer in answers:
		print(*answer)