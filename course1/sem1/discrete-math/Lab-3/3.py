n = int(input())
arr = [[0] * n for _ in range(3**n)]

curr = 3**n
count = 0
for i in range(n):
	count = 0
	for j in range(3**n):
		arr[j][i] = count
		count += 1
		if (j + 1) % curr == 0:
			if count == 3:
				count = 1
			elif count == 2:
				count = 0
			elif count == 1:
				count = 2
		elif count > 2:
			count = 0
	curr //= 3

for i in range(3**n):
	print("".join(map(str, arr[i])))