import sys

MOD = 104857601


def main():
	degree_k, index_n = map(int, sys.stdin.readline().split())
	index_n -= 1

	sequence_a = [0] * (2 * degree_k)
	polynomial_q = [0] * (degree_k + 1)

	sequence_input = list(map(int, sys.stdin.readline().split()))
	for i in range(degree_k):
		sequence_a[i] = sequence_input[i]

	polynomial_q[0] = 1
	coefficients_input = list(map(int, sys.stdin.readline().split()))
	for i in range(1, degree_k + 1):
		polynomial_q[i] = normalize_mod(-coefficients_input[i - 1])

	result = compute_nth_term(index_n, degree_k, sequence_a, polynomial_q)
	print(result)


def compute_nth_term(index_n, degree_k, sequence_a, polynomial_q):
	while index_n >= degree_k:
		for i in range(degree_k, 2 * degree_k):
			sequence_a[i] = 0
			for j in range(1, len(polynomial_q)):
				sequence_a[i] = normalize_mod(sequence_a[i] - polynomial_q[j] * sequence_a[i - j])

		coefficients_r = multiply_and_reduce_polynomials(polynomial_q, degree_k)
		polynomial_q = coefficients_r[:]
		filter_sequence(sequence_a, index_n)
		index_n //= 2

	return sequence_a[index_n]


def multiply_and_reduce_polynomials(polynomial_q, degree_k):
	negative_q = [normalize_mod(polynomial_q[i] if i % 2 == 0 else -polynomial_q[i]) for i in range(len(polynomial_q))]
	coefficients_r = [0] * (degree_k + 1)
	for i in range(0, 2 * degree_k + 1, 2):
		coefficient_r = 0
		for j in range(0, i + 1):
			qq = polynomial_q[j] if j <= degree_k else 0
			neq = negative_q[i - j] if i - j <= degree_k else 0
			coefficient_r = normalize_mod(coefficient_r + qq * neq)
		coefficients_r[i // 2] = coefficient_r
	return coefficients_r


def filter_sequence(sequence_a, index_n):
	free_index = 0
	for i in range(len(sequence_a)):
		if i % 2 == index_n % 2:
			sequence_a[free_index] = sequence_a[i]
			free_index += 1


def normalize_mod(value):
	return ((value % MOD) + MOD) % MOD


if __name__ == '__main__':
	main()
