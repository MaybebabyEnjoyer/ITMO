import java.util.Scanner;

public class B {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] array = new int[n];
        for (int i = 0; i < n; i++) {
            array[i] = sc.nextInt();
        }
        int[] tree = new int[4 * n];
        build(array, tree, 0, 0, n);
        while (sc.hasNext()) {
            String operation = sc.next();
            if (operation.equals("set")) {
                int i = sc.nextInt() - 1;
                int x = sc.nextInt();
                set(i, x, 0, 0, n, tree);
            } else {
                int i = sc.nextInt() - 1;
                int j = sc.nextInt();
                System.out.println(min(i, j, 0, 0, n, tree));
            }
        }
    }

    static void build(int[] array, int[] tree, int v, int vl, int vr) {
        if (vl + 1 == vr) {
            tree[v] = array[vl];
            return;
        }
        int vm = vl + (vr - vl) / 2;
        build(array, tree, 2 * v + 1, vl, vm);
        build(array, tree, 2 * v + 2, vm, vr);
        tree[v] = Math.min(tree[2 * v + 1], tree[2 * v + 2]);
    }

    static void set(int i, int x, int v, int vl, int vr, int[] tree) {
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
        tree[v] = Math.min(tree[2 * v + 1], tree[2 * v + 2]);
    }

    static int min(int l, int r, int v, int vl, int vr, int[] tree) {
        if (l >= vr || vl >= r) {
            return Integer.MAX_VALUE;
        }
        if (vl >= l && r >= vr) {
            return tree[v];
        }
        int vm = vl + (vr - vl) / 2;
        int min1 = min(l, r, 2 * v + 1, vl, vm, tree);
        int min2 = min(l, r, 2 * v + 2, vm, vr, tree);
        return Math.min(min1, min2);
    }
}