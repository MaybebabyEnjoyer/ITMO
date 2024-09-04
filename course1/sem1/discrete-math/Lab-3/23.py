def get_next_binary_string(n: int, s: str) -> str:
    s = list(s)
    for i in range(n - 1, -1, -1):
        if s[i] == "0":
            s[i] = "1"
            break
        else:
            s[i] = "0"
    return "".join(s)


def get_prev_binary_string(n: int, s: str) -> str:
    s = list(s)
    for i in range(n - 1, -1, -1):
        if s[i] == "1":
            s[i] = "0"
            break
        else:
            s[i] = "1"
    return "".join(s)


if __name__ == '__main__':
    s = input()
    prev = get_prev_binary_string(len(s), s)
    next = get_next_binary_string(len(s), s)
    if prev == "1" * len(s):
        prev = "-"
    if next == "0" * len(s):
        next = "-"
    print(prev)
    print(next)
