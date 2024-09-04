def next_bracket(s: str):
    n = len(s)
    i = n - 1
    while i > 0 and s[i] == ')':
        i -= 1
    if i == 0:
        return None
    s = s[:i] + ')' + s[i + 1:]
    j = i + 1
    while j < n and s[j] == ')':
        j += 1
    s = s[:j] + '(' + s[j + 1:]
    return s


s = input()
s = next_bracket(s)
print(s)