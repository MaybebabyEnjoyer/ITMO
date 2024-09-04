def get_next_partitions(arr: list) -> list:
	arr[-1] -= 1
	arr[-2] += 1
	if arr[-2] > arr[-1]:
		arr[-2] += arr[-1]
		arr.pop()
	else:
		while arr[-2] * 2 <= arr[-1]:
			arr.append(arr[-1] - arr[-2])
			arr[-2] = arr[-3]
	return arr


if __name__ == '__main__':
	s = input().split("=")
	n = int(s[0])
	arr = [int(x) for x in s[1].split("+")]
	if n == arr[0]:
		print("No solution")
	else:
		print("=".join([str(n), "+".join(map(str, get_next_partitions(arr)))]))
