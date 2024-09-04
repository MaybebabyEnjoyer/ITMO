def binary_codes(n):
    if n == 0:
        return ['']
    else:
        return [digit + code
                for digit in ['0', '1']
                for code in binary_codes(n - 1)]


if __name__ == '__main__':
    n = int(input())
    print(*binary_codes(n), sep='\n')
