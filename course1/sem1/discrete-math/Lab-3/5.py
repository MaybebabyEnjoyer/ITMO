def generate_vectors(n, k):
	if n == 0:
		return [[]]
	else:
		return [vector[:i] + [digit] + vector[i:]
		        for vector in generate_vectors(n - 1, k)
		        for i in range(n)
		        for digit in range(k)]


# n, k = map(int, input().split())
n, k = 2, 2
answers = sorted(generate_vectors(n, k))
answers = sorted(set(map(lambda x: ''.join(map(str, x)), answers)))
