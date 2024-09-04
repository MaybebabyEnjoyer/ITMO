from typing import List


def lzw_coding(s: str) -> List[int]:
    d = {}
    for i in range(65, 91):
        d[chr(i)] = i - 65
    code = 26
    w = ''
    result = []
    for c in s:
        wc = w + c
        if wc in d:
            w = wc
        else:
            result.append(d[w])
            d[wc] = code
            code += 1
            w = c
    if w:
        result.append(d[w])
    return result


if __name__ == '__main__':
    print(*lzw_coding(input().upper()))