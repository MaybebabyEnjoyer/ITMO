from typing import List


def lzw_decoding(l: List[int]) -> str:
    d = {}
    for i in range(65, 91):
        d[i - 65] = chr(i)
    code = 26
    w = chr(l[0] + 65).lower()
    result = w
    for k in l[1:]:
        if k in d:
            entry = d[k]
        elif k == code:
            entry = w + w[0]
        else:
            raise ValueError(f'Bad compressed k: {k}')
        result += entry
        d[code] = w + entry[0]
        code += 1
        w = entry
    return "".join(result.lower())


if __name__ == '__main__':
    n = int(input())
    input_array = [int(i) for i in input().split()]
    print(lzw_decoding(input_array))