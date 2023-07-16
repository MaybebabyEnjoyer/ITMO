import java.util.Scanner;

public class Task1B {

    private static class Node {
        int maxInRange;
        int maxCount;

        public Node(int maxInRange, int maxCount) {
            this.maxInRange = maxInRange;
            this.maxCount = maxCount;
        }
    }

    public static Node combine(Node leftChild, Node rightChild) {
        if (leftChild.maxInRange > rightChild.maxInRange) {
            return new Node(leftChild.maxInRange, leftChild.maxCount);
        } else if (leftChild.maxInRange < rightChild.maxInRange) {
            return new Node(rightChild.maxInRange, rightChild.maxCount);
        } else {
            return new Node(rightChild.maxInRange, rightChild.maxCount + leftChild.maxCount);
        }
    }

    public static void build(Node[] tree, int[] a, int v, int l, int r) {
        if (l == r) {
            tree[v] = new Node(a[l], 1);
        } else {
            int m = (l + r) / 2;
            build(tree, a, v * 2 + 1, l, m);
            build(tree, a, v * 2 + 2, m + 1, r);
            tree[v] = combine(tree[v * 2 + 1], tree[v * 2 + 2]);
        }
    }

    public static Node get(Node[] tree, int v, int l, int r, int ql, int qr) {
        if (qr <= l || ql >= r) {
            return new Node(Integer.MIN_VALUE, -1);
        }
        if (ql <= l && r <= qr) {
            return tree[v];
        }
        int m = (l + r) / 2;
        Node leftChild = get(tree, 2 * v + 1, l, m, ql, qr);
        Node rightChild = get(tree, 2 * v + 2, m, r, ql, qr);
        return combine(leftChild, rightChild);
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
            int left = sc.nextInt();
            int right = sc.nextInt();
            Node res = get(tree, 1, 0, pow, left - 1, right);
            ans.append(res.maxInRange).append(" ").append(res.maxCount).append('\n');
            k--;
        }
        System.out.println(ans);
    }
}