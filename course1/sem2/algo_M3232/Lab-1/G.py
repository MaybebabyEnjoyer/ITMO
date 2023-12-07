def push(v, vl, vr, tree):
    if vl + 1 == vr:
        return
    tree[2 * v + 1] = max(tree[v], tree[2 * v + 1])
    tree[2 * v + 2] = max(tree[v], tree[2 * v + 2])
    tree[v] = min(tree[2 * v + 1], tree[2 * v + 2])


def globalPush(v, vl, vr, tree):
    if vl + 1 == vr:
        return
    push(v, vl, vr, tree)
    vm = vl + (vr - vl) // 2
    globalPush(2 * v + 1, vl, vm, tree)
    globalPush(2 * v + 2, vm, vr, tree)


def set(l, r, x, v, vl, vr, tree):
    if l >= vr or vl >= r:
        return
    if vl >= l and r >= vr:
        tree[v] = max(tree[v], x)
        return
    push(v, vl, vr, tree)
    vm = vl + (vr - vl) // 2
    set(l, r, x, 2 * v + 1, vl, vm, tree)
    set(l, r, x, 2 * v + 2, vm, vr, tree)
    tree[v] = min(tree[2 * v + 1], tree[2 * v + 2])


def minimum(l, r, v, vl, vr, tree):
    if l >= vr or vl >= r:
        return 2 ** 31 - 1
    if vl >= l and r >= vr:
        return tree[v]
    vm = vl + (vr - vl) // 2
    min1 = minimum(l, r, 2 * v + 1, vl, vm, tree)
    min2 = minimum(l, r, 2 * v + 2, vm, vr, tree)
    return min(min1, min2)


with open("rmq.in") as f_in:
    n, m = map(int, f_in.readline().split())
    size = 1
    while size < n:
        size *= 2
    tree = [-(2 ** 31) for _ in range(2 * size)]
    requests = []
    for i in range(m):
        left, right, x = map(int, f_in.readline().split())
        requests.append((left, right, x))
        set(left - 1, right, x, 0, 0, size, tree)
f_in.close()
globalPush(0, 0, size, tree)
with open("rmq.out", 'w') as f_out:
    for i in range(m):
        verdict = "consistent"
        if minimum(requests[i][0] - 1, requests[i][1], 0, 0, size, tree) != requests[i][2]:
            verdict = "inconsistent"
            break
    f_out.write(verdict + '\n')
    if verdict == "consistent":
        for i in range(size - 1, size + n - 1):
            f_out.write(str(tree[i]) + " ")
f_out.close()
