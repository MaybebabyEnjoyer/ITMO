def next_partition(a: list[list]) -> list[list]:
	used = []
	fl = False
	for i in reversed(range(len(a))):
		if len(used) != 0 and max(used) > a[i][-1]:
			m = min([x for x in used if x > a[i][-1]])
			a[i].append(m)
			used.remove(m)
			break
		for j in reversed(range(len(a[i]))):
			if len(used) != 0 and j != 0 and max(used) > a[i][j]:
				m = min([x for x in used if x > a[i][j]])
				old = a[i][j]
				a[i][j] = m
				used.remove(m)
				used.append(old)
				fl = True
				break
			else:
				used.append(a[i][-1])
				a[i].pop()
				if len(a[i]) == 0:
					a.pop()
		if fl:
			break
	used.sort()
	for i in range(len(used)):
		a.append([used[i]])
	return a


if __name__ == '__main__':
	flag = True
	while flag:
		n, k = map(int, input().split())
		if n == 0 and k == 0:
			flag = False
		else:
			a = [[int(x) for x in input().split()] for _ in range(k)]
			res = next_partition(a)
			for item in res:
				print(*item)
