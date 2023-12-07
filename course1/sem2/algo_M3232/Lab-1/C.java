import java.util.Scanner;

public class C {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        long[] array = new long[n];
        for (int i = 0; i < n; i++) {
            array[i] = sc.nextLong();
        }
        long[] tree = new long[4 * n];
        build(array, tree, 0, 0, n);
        while (sc.hasNext()) {
            String operation = sc.next();
            if (operation.equals("set")) {
                int i = sc.nextInt() - 1;
                long x = sc.nextLong();
                set(i, x, 0, 0, n, tree);
            } else {
                int i = sc.nextInt() - 1;
                int j = sc.nextInt();
                System.out.println(sum(i, j, 0, 0, n, tree));
            }
        }
    }

    static void build(long[] array, long[] tree, int v, int vl, int vr) {
        if (vl + 1 == vr) {
            tree[v] = array[vl];
            return;
        }
        int vm = vl + (vr - vl) / 2;
        build(array, tree, 2 * v + 1, vl, vm);
        build(array, tree, 2 * v + 2, vm, vr);
        tree[v] = tree[2 * v + 1] + tree[2 * v + 2];
    }

    static void set(int i, long x, int v, int vl, int vr, long[] tree) {
        if (vl + 1 == vr) {
            tree[v] = x;
            return;
        }
        int vm = vl + (vr - vl) / 2;
        if (i < vm) {
            set(i, x, 2 * v + 1, vl, vm, tree);
        } else {
            set(i, x, 2 * v + 2, vm, vr, tree);
        }
        tree[v] = tree[2 * v + 1] + tree[2 * v + 2];
    }

    static long sum(int l, int r, int v, int vl, int vr, long[] tree) {
        if (l >= vr || vl >= r) {
            return 0;
        }
        if (vl >= l && r >= vr) {
            return tree[v];
        }
        int vm = vl + (vr - vl) / 2;
        long sum1 = sum(l, r, 2 * v + 1, vl, vm, tree);
        long sum2 = sum(l, r, 2 * v + 2, vm, vr, tree);
        return sum1 + sum2;
    }
}