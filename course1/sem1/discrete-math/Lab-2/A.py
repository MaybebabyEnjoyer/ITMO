def main():
    n = int(input())
    arr = [int(x) for x in input().split()]
    count = 0
    arr_length = n - 1
    arr = sorted(arr, reverse=True)
    while arr_length > 0:
        arr[arr_length] += arr[arr_length - 1]
        count += arr[arr_length]
        arr = sorted(arr, reverse=True)
        arr_length -= 1
    print(count)


if __name__ == '__main__':
    main()
