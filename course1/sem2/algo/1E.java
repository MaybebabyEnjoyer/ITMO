import java.util.Scanner;

public class Task1E {

    private static class Node {
        int maxInRange;

        public Node(int maxInRange) {
            this.maxInRange = maxInRange;
        }
    }

    public static Node combine(Node leftChild, Node rightChild) {
        return new Node(Math.max(leftChild.maxInRange, rightChild.maxInRange));
    }

    public static void build(Node[] tree, int[] a, int v, int l, int r) {
        if (l == r) {
            tree[v] = new Node(a[l]);
        } else {
            int m = (l + r) / 2;
            build(tree, a, v * 2 + 1, l, m);
            build(tree, a, v * 2 + 2, m + 1, r);
            tree[v] = combine(tree[v * 2 + 1], tree[v * 2 + 2]);
        }
    }

    public static int get(Node[] tree, int v, int l, int r, int ql, int qr, int k) {
        if (qr <= l || ql >= r) {
            return -1;
        }
        if (ql <= l && r <= qr) {
            if (tree[v].maxInRange < k) {
                return -1;
            }
            while (r - l > 1) {
                int m = (l + r) / 2;
                if (tree[2 * v + 1].maxInRange >= k) {
                    v = 2 * v + 1;
                    r = m;
                } else {
                    v = 2 * v + 2;
                    l = m;
                }
            }
            return l + 1;
        }
        int m = (l + r) / 2;
        int res = get(tree, 2 * v + 1, l, m, ql, qr, k);
        if (res != -1) {
            return res;
        }
        return get(tree, 2 * v + 2, m, r, ql, qr, k);
    }

    /*public static Node get(Node[] tree, int v, int l, int r, int ql, int qr) {
        if (qr <= l || ql >= r) {
            return new Node(Integer.MIN_VALUE);
        }
        if (ql <= l && r <= qr) {
            return tree[v];
        }
        int m = (l+r)/2;
        Node leftChild = get(tree, 2*v + 1, l, m, ql, qr);
        Node rightChild = get(tree,2*v + 2, m, r, ql, qr);
        return combine(leftChild, rightChild);
    }*/

    public static void set(Node[] tree, int v, int l, int r, int i, int val) {
        if (r - l == 1) {
            tree[v] = new Node(val);
            return;
        }
        int m = (r + l) / 2;
        if (i < m) {
            set(tree, 2 * v + 1, l, m, i, val);
        } else {
            set(tree, 2 * v + 2, m, r, i, val);
        }
        tree[v] = combine(tree[2 * v + 1], tree[2 * v + 2]);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int m = sc.nextInt();
        int pow = 1 << 32 - Integer.numberOfLeadingZeros(n - 1);
        int[] array = new int[pow];
        for (int i = 0; i < n; i++) {
            array[i] = sc.nextInt();
        }
        for (int i = n; i < pow; i++) {
            array[i] = Integer.MIN_VALUE;
        }
        Node[] tree = new Node[4 * pow];
        build(tree, array, 1, 0, pow - 1);
        StringBuilder ans = new StringBuilder();
        while (m > 0) {
            int t = sc.nextInt();
            int f = sc.nextInt();
            int s = sc.nextInt();
            if (t == 0) {
                set(tree, 1, 0, pow, f - 1, s);
            } else if (t == 1) {
                ans.append(get(tree, 1, 0, pow, f - 1, pow, s)).append('\n');
            }
            m--;
        }
        System.out.println(ans);
    }
}