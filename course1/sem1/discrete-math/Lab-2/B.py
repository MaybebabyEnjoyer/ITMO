def BWT(string: str) -> str:
	changes = sorted(string[i:] + string[:i] for i in range(len(string)))
	return ''.join(change[-1] for change in changes)


if __name__ == '__main__':
	print(BWT(input()))
