from string import ascii_lowercase
from typing import List


def MoveToFront(string: str) -> List[int]:
	alphabet = ascii_lowercase
	result = []
	for i in range(len(string)):
		result.append(alphabet.index(string[i]) + 1)
		alphabet = alphabet[alphabet.index(string[i])] + alphabet[:alphabet.index(string[i])] + alphabet[alphabet.index(string[i]) + 1:]

	return result


if __name__ == '__main__':
	res = MoveToFront(input())
	print(*res)