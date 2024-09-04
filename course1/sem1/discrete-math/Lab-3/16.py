from functools import lru_cache


@lru_cache(maxsize=None)
def choose(n, k):
	if k == 0:
		return 1
	if n <= 0 or k < 0:
		return 0
	return choose(n, k - 1) * (n - k + 1) // k


def choose_to_number(n: int, k: int, need: list) -> int:
	m = 0
	mius = 1
	l = n
	for i in range(l):
		if mius in need:
			need.remove(mius)
			k -= 1
		else:
			m += choose(n - 1, k - 1)
		n -= 1
		mius += 1
	return m


if __name__ == '__main__':
	n, k = map(int, input().split())
	need = [int(i) for i in input().split()]
	print(choose_to_number(n, k, need))
