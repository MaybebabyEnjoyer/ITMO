import java.io.*;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;

public class L {
    public static void main(String[] args) {
        File input = new File("parking.in");
        File output = new File("parking.out");
        try {
            try (Scanner sc = new Scanner(input)) {
                try {
                    try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), StandardCharsets.UTF_8))) {
                        int n = sc.nextInt();
                        int[] tree = new int[4 * n];
                        build(tree, 0, 0, n);
                        int m = sc.nextInt();
                        int i, x;
                        String operation;
                        for (int t = 0; t < m; t++) {
                            operation = sc.next();
                            x = sc.nextInt();
                            if (operation.equals("enter")) {
                                i = min(x - 1, n, 0, 0, n, tree);
                                if (i == Integer.MAX_VALUE) {
                                    i = min(0, x, 0, 0, n, tree);
                                }
                                bw.write(String.valueOf(i));
                                bw.newLine();
                                set(i - 1, Integer.MAX_VALUE, 0, 0, n, tree);
                            } else {
                                i = x - 1;
                                set(i, x, 0, 0, n, tree);
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    static void build(int[] tree, int v, int vl, int vr) {
        if (vl + 1 == vr) {
            tree[v] = vl + 1;
            return;
        }
        int vm = vl + (vr - vl) / 2;
        build(tree, 2 * v + 1, vl, vm);
        build(tree, 2 * v + 2, vm, vr);
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
