import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Task1G {

    public static void build(int[] tree, int[] a, int v, int l, int r) {
        if (r == l) {
            tree[v] = a[l];
        } else {
            int m = (l + r) / 2;
            build(tree, a, v * 2 + 1, l, m);
            build(tree, a, v * 2 + 2, m + 1, r);
            tree[v] = tree[v * 2 + 1] + tree[v * 2 + 2];
        }
    }

    public static int get(int[] tree, int v, int l, int r, int ql, int qr) {
        if (qr <= l || ql >= r) {
            return 0;
        }
        if (ql <= l && r <= qr) {
            return tree[v];
        }
        int m = (l + r) / 2;
        int leftChild = get(tree, 2 * v + 1, l, m, ql, qr);
        int rightChild = get(tree, 2 * v + 2, m, r, ql, qr);
        return leftChild + rightChild;
    }

    public static void add(int[] tree, int v, int l, int r, int i, int val) {
        if (r - l == 1) {
            tree[v] += val;
            return;
        }
        int m = (r + l) / 2;
        if (i < m) {
            add(tree, 2 * v + 1, l, m, i, val);
        } else {
            add(tree, 2 * v + 2, m, r, i, val);
        }
        tree[v] = tree[2 * v + 1] + tree[2 * v + 2];
    }

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            int n = Integer.parseInt(reader.readLine());
            int pow = 1 << 32 - Integer.numberOfLeadingZeros(n - 1);
            int[] array = new int[n];
            String[] s = reader.readLine().split(" ");
            int[] zeroes = new int[pow];
            for (int i = 0; i < n; i++) {
                array[i] = Integer.parseInt(s[i]);
            }
            int[] finalArray = Arrays.copyOf(array, array.length);
            Arrays.sort(array);
            Map<Integer, Integer> map = new HashMap<>();
            for (int i = 0; i < n; i++) {
                if (!map.containsKey(array[i])) {
                    map.put(array[i], map.size());
                }
            }
            for (int i = 0; i < n; i++) {
                finalArray[i] = map.get(finalArray[i]);
            }
            int[] treeZero = new int[4 * pow];
            build(treeZero, zeroes, 1, 0, pow - 1);
            long[] inversionCount1 = new long[n];
            for (int i = 0; i < n; i++) {
                inversionCount1[i] += get(treeZero, 1, 0, pow, finalArray[i] + 1, pow);
                add(treeZero, 1, 0, pow, finalArray[i], 1);
            }
            treeZero = new int[4 * pow];
            build(treeZero, zeroes, 1, 0, pow - 1);
            long[] inversionCount2 = new long[n];
            for (int i = n - 1; i >= 0; i--) {
                inversionCount2[i] += get(treeZero, 1, 0, pow, 0, finalArray[i]);
                add(treeZero, 1, 0, pow, finalArray[i], 1);
            }
            long ans = 0;
            for (int i = 0; i < n; i++) {
                ans += inversionCount1[i] * inversionCount2[i];
            }
            System.out.println(ans);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}