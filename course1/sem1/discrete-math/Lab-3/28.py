def get_next_multipermutation(arr: list[int]):
	n = len(arr)
	i = n - 2
	while i >= 0 and arr[i] >= arr[i + 1]:
		i -= 1
	if i >= 0:
		j = i + 1
		while j < n - 1 and arr[j + 1] > arr[i]:
			j += 1
		arr[i], arr[j] = arr[j], arr[i]
		arr[i + 1:] = arr[n - 1:i:-1]
		return arr
	else:
		return [0 for i in range(n)]


if __name__ == '__main__':
	n = int(input())
	arr = [int(x) for x in input().split()]
	print(" ".join(map(str, get_next_multipermutation(arr))))