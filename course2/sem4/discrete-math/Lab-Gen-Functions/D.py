from math import gcd, factorial


def pow_bin(value: int, power: int) -> int:
	result = 1
	while power > 0:
		if power % 2 == 1:
			result *= value
		value *= value
		power //= 2
	return result


def main():
	r, k = map(int, input().split())
	p = [int(x) for x in input().split()]

	f = [[0 for _ in range(k + 1)] for _ in range(k + 1)]
	for i in range(k + 1):
		f[i][0] = 1
		f[i][1] = 0
		for j in range(k, 0, -1):
			current = f[i][:]
			tmp = [0, 1]
			tmp[0] = -i + j
			for l in range(k + 1):
				f[i][l] = current[l] * tmp[0] + (current[l - 1] * tmp[1] if l > 0 else 0)

	result_a = [0 for _ in range(k + 1)]
	result_b = [0 for _ in range(k + 1)]
	for i in range(k + 1):
		for j in range(k + 1):
			result_a[j] += f[i][j] * p[i] * pow_bin(r, k - i)
		result_b[i] = pow_bin(r, k) * factorial(k)

	for i in range(k + 1):
		current_gcd = gcd(result_a[i], result_b[i])
		numerator = result_a[i] // current_gcd
		denominator = result_b[i] // current_gcd
		print(f"{numerator}/{denominator}", end=' ')


if __name__ == "__main__":
	main()
