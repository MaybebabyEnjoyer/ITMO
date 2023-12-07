n, x, y, a_ = map(int, input().split())
prefix = [0]
for i in range(1, n + 1):
    prefix.append(prefix[i - 1] + a_)
    a_ = (x * a_ + y) % 2 ** 16
m, z, t, b_ = map(int, input().split())
count = 0
for i in range(m):
    c1 = b_ % n
    b_ = (z * b_ + t) % 2 ** 30
    c2 = b_ % n
    b_ = (z * b_ + t) % 2 ** 30
    l = min(c1, c2)
    r = max(c1, c2)
    count += prefix[r + 1] - prefix[l]
print(count)
