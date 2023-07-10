def inversion(arr):
    total = 0
    count = [0] * (len(arr) + 1)
    tree = { x: i + 1 for i, x in enumerate(sorted(arr)) }
    for x in reversed(arr):
        i = tree[x] - 1
        while i:
            total += count[i]
            i -= i & -i
        i = tree[x]
        while i <= len(arr):
            count[i] += 1
            i += i & -i
    return total

n = int(input())
arr = []
for i in input().split(" "):
    arr.append(int(i))
print(inversion(arr))