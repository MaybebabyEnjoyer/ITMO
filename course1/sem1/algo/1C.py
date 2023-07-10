from math import floor

def antiQuickSort(arr, k):
    for i in range(2, k):
        arr[i], arr[i // 2] = arr[i // 2], arr[i]
    return arr
k = int(input())
glist = []

for i in range(k):
    glist.append(i+1)
print(*(antiQuickSort(glist, k)))