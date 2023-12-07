n, k = map(int, input().split())
money = [0] + [int(x) for x in input().split()] + [0]
dynamics = [0] + [None for _ in range(n)]
prev = [-1] + [None for _ in range(n)] 
for i in range(1, n):
    j = i - k if i - k > 0 else 0
    mx = -10001
    while j < i:
        if (mx < dynamics[j]):
            mx = dynamics[j]
            prev[i] = j
        j += 1        
    dynamics[i] = money[i] + mx
print(dynamics[n - 1])
pos = n - 1
count = 0
answer = []
while pos != -1:
    count += 1
    answer.append(pos + 1)
    pos = prev[pos]
print(count - 1)
answer.reverse()
print(*answer)