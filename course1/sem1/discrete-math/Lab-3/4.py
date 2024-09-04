from functools import lru_cache


def left_str_shift(s, n):
    return s[n:] + s[:n]


@lru_cache(maxsize=None)
def chain_code(n):
    got = set()
    codes = ["0" * n]
    got.add(codes[0])
    i = 0
    previous_code = codes[0]
    while True:
        new_code = left_str_shift(previous_code, 1)
        new_code = new_code[:n - 1] + "1"
        if new_code not in got:
            got.add(new_code)
            codes.append(new_code)
        else:
            new_code = new_code[:n - 1] + "0"
            if new_code not in got:
                got.add(new_code)
                codes.append(new_code)
            else:
                return codes
        previous_code = new_code


if __name__ == '__main__':
    n = int(input())
    for item in chain_code(n):
        print(item)
