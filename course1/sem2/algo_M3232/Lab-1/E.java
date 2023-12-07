import java.util.Scanner;

public class E {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] array = new int[n];
        for (int i = 0; i < n; i++) {
            array[i] = sc.nextInt();
        }
        int[] tree = new int[4 * n];
        build(array, tree, 0, 0, n);
        int m = sc.nextInt();
        for (int t = 0; t < m; t++) {
            int operation = sc.nextInt();
            if (operation == 0) {
                int i = sc.nextInt() - 1;
                int j = sc.nextInt();
                set(i, j, 0, 0, n, tree);
            } else {
                int l = sc.nextInt() - 1;
                int r = sc.nextInt();
                System.out.println(sum(l, r, 0, 0, n, tree));
            }
        }
    }

    static void build(int[] array, int[] tree, int v, int vl, int vr) {
        if (vl + 1 == vr) {
            tree[v] = vl % 2 == 0 ? array[vl] : -array[vl];
            return;
        }
        int vm = vl + (vr - vl) / 2;
        build(array, tree, 2 * v + 1, vl, vm);
        build(array, tree, 2 * v + 2, vm, vr);
        tree[v] = tree[2 * v + 1] + tree[2 * v + 2];
    }

    static void set(int i, int j, int v, int vl, int vr, int[] tree) {
        if (vl + 1 == vr) {
            tree[v] = vl % 2 == 0 ? j : -j;
            return;
        }
        int vm = vl + (vr - vl) / 2;
        if (i < vm) {
            set(i, j, 2 * v + 1, vl, vm, tree);
        } else {
            set(i, j, 2 * v + 2, vm, vr, tree);
        }
        tree[v] = tree[2 * v + 1] + tree[2 * v + 2];
    }

    static long sum(int l, int r, int v, int vl, int vr, int[] tree) {
        if (l >= vr || vl >= r) {
            return 0;
        }
        if (vl >= l && r >= vr) {
            return l % 2 == 0 ? tree[v] : -tree[v];
        }
        int vm = vl + (vr - vl) / 2;
        long s1 = sum(l, r, 2 * v + 1, vl, vm, tree);
        long s2 = sum(l, r, 2 * v + 2, vm, vr, tree);
        return s1 + s2;
    }
}
