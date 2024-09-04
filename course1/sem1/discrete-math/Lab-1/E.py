from typing import List


def binary_pascal_triangle(arr: List[int]) -> List[int]:
    result = [_ for _ in range(len(arr) - 1)]
    for i in range(len(arr) - 1):
        a = arr[i]
        b = arr[i + 1]
        result[i] = int(a) ^ int(b)
    return result


def main():
    n = int(input())
    arr = [[str(_) for _ in input().split()] for _ in range(2 ** n)]
    input_func_results = [arr[i][1] for i in range(2 ** n)]
    zhegalkin_polynomial = [input_func_results[0]]

    for i in range(len(arr) - 1, 0, -1):
        input_func_results = binary_pascal_triangle(input_func_results)
        zhegalkin_polynomial.append(input_func_results[0])

    for i in range(len(arr)):
        print(arr[i][0], zhegalkin_polynomial[i])


if __name__ == '__main__':
    main()
