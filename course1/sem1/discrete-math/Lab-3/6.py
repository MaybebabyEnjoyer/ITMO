def binary_codes(n):
	if n == 0:
		return ['']
	else:
		return [digit + code
		        for digit in ['0', '1']
		        for code in binary_codes(n - 1)]


def filter_codes(codes):
	count = 0
	for code in codes:
		if "11" not in code:
			count += 1
			yield code
	print(count)


def main():
	n = int(input())
	print(*filter_codes(binary_codes(n)), sep='\n')


if __name__ == '__main__':
	main()
