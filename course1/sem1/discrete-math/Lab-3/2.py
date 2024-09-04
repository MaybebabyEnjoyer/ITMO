def gray_codes(n):
    if n == 0:
        return ['']
    else:
        codes = gray_codes(n - 1)
        return ['0' + code for code in codes] + ['1' + code for code in reversed(codes)]


if __name__ == '__main__':
    n = int(input())
    print(*gray_codes(n), sep='\n')
