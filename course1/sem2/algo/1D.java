import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Task1D {

    private static class Node {
        long leftZ;
        long rightZ;

        int total;

        public Node(long leftZ, long rightZ, int total) {
            this.leftZ = leftZ;
            this.rightZ = rightZ;
            this.total = total;
        }
    }

    public static Node combine(Node leftChild, Node rightChild) {
        return new Node(leftChild.total, rightChild.total, leftChild.total + rightChild.total);
    }

    public static void build(Node[] tree, long[] a, int v, int l, int r) {
        if (l == r) {
            if (a[l] == 0) {
                tree[v] = new Node(1, 0, 1);
            } else {
                tree[v] = new Node(0, 0, 0);
            }
        } else {
            int m = (l + r) >> 1;
            build(tree, a, v * 2 + 1, l, m);
            build(tree, a, v * 2 + 2, m + 1, r);
            tree[v] = combine(tree[v * 2 + 1], tree[v * 2 + 2]);
        }
    }

    public static Node get(Node[] tree, int v, int l, int r, int ql, int qr) {
        if (qr <= l || ql >= r) {
            return new Node(0, 0, 0);
        }
        if (ql <= l && r <= qr) {
            return tree[v];
        }
        int m = (l + r) >> 1;
        Node leftChild = get(tree, 2 * v + 1, l, m, ql, qr);
        Node rightChild = get(tree, 2 * v + 2, m, r, ql, qr);
        return combine(leftChild, rightChild);
    }

    public static int query(Node[] tree, int v, int l, int r, int k) {
        if (r - l == 1) {
            return l + 1;
        }
        int m = (l + r) >> 1;
        if (tree[2 * v + 1].total >= k) {
            return query(tree, 2 * v + 1, l, m, k);
        } else {
            return query(tree, 2 * v + 2, m, r, k - tree[2 * v + 1].total);
        }
    }

    public static void set(Node[] tree, int v, int l, int r, int pos, int val) {
        if (r - l == 1) {
            if (val == 0) {
                tree[v] = new Node(1, 0, 1);
            } else {
                tree[v] = new Node(0, 0, 0);
            }
            return;
        }
        int m = (r + l) >> 1;
        if (pos < m) {
            set(tree, 2 * v + 1, l, m, pos, val);
        } else {
            set(tree, 2 * v + 2, m, r, pos, val);
        }
        tree[v] = combine(tree[2 * v + 1], tree[2 * v + 2]);
    }

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            int n = Integer.parseInt(reader.readLine());
            int pow = 1 << 32 - Integer.numberOfLeadingZeros(n - 1);
            long[] array = new long[pow];
            String[] temp = reader.readLine().split(" ");
            for (int i = 0; i < n; i++) {
                array[i] = Integer.parseInt(temp[i]);
            }
            int m = Integer.parseInt(reader.readLine());
            Node[] tree = new Node[4 * pow];
            build(tree, array, 1, 0, pow - 1);
            StringBuilder ans = new StringBuilder();
            while (m > 0) {
                temp = reader.readLine().split(" ");
                if (temp[0].equals("s")) {
                    int l = Integer.parseInt(temp[1]) - 1;
                    int r = Integer.parseInt(temp[2]);
                    int k = Integer.parseInt(temp[3]);
                    int curr = get(tree, 1, 0, pow, l, r).total;
                    if (curr < k) {
                        ans.append("-1 ");
                    } else {
                        ans.append(query(tree, 1, 0, pow, k + get(tree, 1, 0, pow, 0, r).total - curr)).append(" ");
                    }
                } else {
                    set(tree, 1, 0, pow, Integer.parseInt(temp[1]) - 1, Integer.parseInt(temp[2]));
                }
                m--;
            }
            System.out.println(ans);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}