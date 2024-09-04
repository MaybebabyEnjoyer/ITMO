from functools import lru_cache


def choose(n, k):
	if k == 0:
		return 1
	return choose(n, k - 1) * (n - k + 1) // k


@lru_cache(maxsize=None)
def number_to_choose(n: int, k: int, m: int) -> list:
	mius = 1
	need = []
	while k > 0:
		if m < choose(n - 1, k - 1):
			need.append(mius)
			k -= 1
		else:
			m -= choose(n - 1, k - 1)
		n -= 1
		mius += 1
	return need


if __name__ == '__main__':
	n, k, m = map(int, input().split())
	print(*number_to_choose(n, k, m))
