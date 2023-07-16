import java.util.Scanner;

public class Task2A {
    private static class Node {
        long max;
        long flag;

        public Node(long maxInRange, long flag) {
            this.max = maxInRange;
            this.flag = flag;
        }
    }

    public static Node combine(Node leftChild, Node rightChild) {
        return new Node(Math.max(leftChild.max, rightChild.max), 0);
    }

    public static void build(Node[] tree, int[] a, int v, int l, int r) {
        if (l == r) {
            tree[v] = new Node(a[l], 0);
        } else {
            int m = (l + r) / 2;
            build(tree, a, v * 2 + 1, l, m);
            build(tree, a, v * 2 + 2, m + 1, r);
            tree[v] = combine(tree[v * 2 + 1], tree[v * 2 + 2]);
        }
    }

    public static Node get(Node[] tree, int v, int l, int r, int ql, int qr) {
        if (qr <= l || ql >= r) {
            return new Node(Integer.MIN_VALUE, 0);
        }
        if (ql <= l && r <= qr) {
            return tree[v];
        }
        int m = (l + r) / 2;
        push(tree, v);
        Node leftChild = get(tree, 2 * v + 1, l, m, ql, qr);
        Node rightChild = get(tree, 2 * v + 2, m, r, ql, qr);
        return combine(leftChild, rightChild);
    }

    public static void push(Node[] tree, int v) {
        tree[2 * v + 1].max += tree[v].flag;
        tree[2 * v + 2].max += tree[v].flag;
        tree[2 * v + 1].flag += tree[v].flag;
        tree[2 * v + 2].flag += tree[v].flag;
        tree[v].flag = 0;
    }

    public static void add(Node[] tree, int v, int l, int r, int ql, int qr, int d) {
        if (qr <= l || ql >= r) {
            return;
        }
        if (ql <= l && r <= qr) {
            tree[v].max += d;
            tree[v].flag += d;
            return;
        }
        int m = (r + l) / 2;
        push(tree, v);
        add(tree, 2 * v + 1, l, m, ql, qr, d);
        add(tree, 2 * v + 2, m, r, ql, qr, d);
        tree[v] = combine(tree[2 * v + 1], tree[2 * v + 2]);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int pow = 1 << 32 - Integer.numberOfLeadingZeros(n - 1);
        int[] array = new int[pow];
        for (int i = 0; i < n; i++) {
            array[i] = sc.nextInt();
        }
        Node[] tree = new Node[4 * pow];
        build(tree, array, 1, 0, pow - 1);
        int k = sc.nextInt();
        StringBuilder ans = new StringBuilder();
        while (k > 0) {
            String mode = sc.next();
            int left = sc.nextInt();
            int right = sc.nextInt();
            if (mode.equals("m")) {
                Node res = get(tree, 1, 0, pow, left - 1, right);
                ans.append(res.max + res.flag).append(" ");
            } else {
                int d = sc.nextInt();
                add(tree, 1, 0, pow, left - 1, right, d);
            }
            k--;
        }
        System.out.println(ans);
    }
}