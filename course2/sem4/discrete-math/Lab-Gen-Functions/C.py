def generate_polynomials(coefficients: list[int], relations: list[int]) -> tuple[list[int], list[int]]:
	num_coeffs = len(coefficients)
	polynomial_q = [1] * (num_coeffs + 1)
	for i in range(1, num_coeffs + 1):
		polynomial_q[i] = -relations[i]

	polynomial_p = [0] * (num_coeffs + 1)
	polynomial_p[0] = coefficients[0]
	for i in range(1, num_coeffs):
		for j in range(1, i + 1):
			polynomial_p[i] += polynomial_q[j] * coefficients[i - j]
		polynomial_p[i] += coefficients[i]

	while polynomial_p and polynomial_p != [0] and polynomial_p[-1] == 0:
		polynomial_p.pop()
	while polynomial_q and polynomial_q[-1] == 0:
		polynomial_q.pop()

	return polynomial_p, polynomial_q


def main():
	num_coeffs = int(input())
	coefficients = list(map(int, input().split()))

	relations = [0] + list(map(int, input().split()))

	p, q = generate_polynomials(coefficients, relations)
	print(len(p) - 1)
	print(' '.join(map(str, p)))
	print(len(q) - 1)
	for value in q:
		print(value, end=' ')


if __name__ == "__main__":
	main()
