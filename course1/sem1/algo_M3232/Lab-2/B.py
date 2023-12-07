arr = [int(x) for x in input().split()]
balls, count = [], []
result = 0
for i in range(1, arr[0] + 1):
    if len(balls) == 0:
        balls.append(arr[i])
        count.append(1)
    elif arr[i] == balls[len(balls) - 1]:
        count[len(count) - 1] += 1
    elif arr[i] != balls[len(balls) - 1]:
        if count[len(count) - 1] >= 3:
            result += count[len(count) - 1]
            del balls[len(balls) - 1]
            del count[len(count) - 1]
        if arr[i] == balls[len(balls) - 1]:
            count[len(count) - 1] += 1
        else:
            balls.append(arr[i])
            count.append(1)            
if count[len(count) - 1] >= 3:
    result += count[len(count) - 1]
print(result)    