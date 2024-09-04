from typing import List


def BWT_decode(n: int, m: int, k: int, s: str) -> List[str]:
	count = [0 for _ in range(m)]
	for i in range(n):
		count[ord(s[i]) - ord('a')] += 1
	summ = 0
	for i in range(m):
		summ = summ + count[i]
		count[i] = summ - count[i]

	vector = [_ for _ in range(n)]
	for i in range(n):
		vector[count[ord(s[i]) - ord('a')]] = i
		count[ord(s[i]) - ord('a')] += 1

	transform = vector[k]
	result = ["" for _ in range(n)]
	for i in range(n):
		result[i] = s[transform]
		transform = vector[transform]

	return result


if __name__ == '__main__':
	s = str(input())
	n = len(s)
	m = len(set(s))
	k = 0
	print("".join(BWT_decode(n, m, k, s)))
