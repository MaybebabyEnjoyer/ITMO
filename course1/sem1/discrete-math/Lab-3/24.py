def next_permutation(s: str) -> str:
    s = [int(x) for x in s.split()]
    for i in range(len(s) - 1, 0, -1):
        if s[i] <= s[i - 1]:
            continue
        else:
            for j in range(len(s) - 1, i - 1, -1):
                if s[j] > s[i - 1]:
                    s[i - 1], s[j] = s[j], s[i - 1]
                    break
            s[i:] = s[i:][::-1]
            return " ".join([str(x) for x in s])
    return " ".join(["0"] * len(s))


def prev_permutation(s: str) -> str:
    s = [int(x) for x in s.split()]
    for i in range(len(s) - 1, 0, -1):
        if s[i] >= s[i - 1]:
            continue
        else:
            for j in range(len(s) - 1, i - 1, -1):
                if s[j] < s[i - 1]:
                    s[i - 1], s[j] = s[j], s[i - 1]
                    break
            s[i:] = s[i:][::-1]
            return " ".join([str(x) for x in s])
    return " ".join(["0"] * len(s))


if __name__ == '__main__':
    n = int(input())
    s = input()
    prev = prev_permutation(s).strip()
    next = next_permutation(s).strip()
    print(prev)
    print(next)
