import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Task1H {

    private static class Node {
        int len;
        int open;
        int close;

        public Node(int len, int open, int close) {
            this.len = len;
            this.open = open;
            this.close = close;
        }
    }

    public static Node combine(Node leftChild, Node rightChild) {
        int temp = Math.min(leftChild.open, rightChild.close);
        return new Node(leftChild.len + rightChild.len + 2 * temp,
                leftChild.open + rightChild.open - temp,
                leftChild.close + rightChild.close - temp);
    }

    public static void build(Node[] tree, int[] a, int v, int l, int r) {
        if (l == r) {
            tree[v] = new Node(0, a[l], 1 - a[l]);
        } else {
            int m = (l + r) >> 1;
            build(tree, a, v * 2 + 1, l, m);
            build(tree, a, v * 2 + 2, m + 1, r);
            tree[v] = combine(tree[v * 2 + 1], tree[v * 2 + 2]);
        }
    }

    public static Node get(Node[] tree, int v, int l, int r, long ql, long qr) {
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

    public static void main(String[] args) {
        try (BufferedReader sc = new BufferedReader(new InputStreamReader(System.in))) {
            String s = sc.readLine();
            int n = s.length();
            int pow = 1 << 32 - Integer.numberOfLeadingZeros(n - 1);
            int[] array = new int[pow];
            for (int i = 0; i < n; i++) {
                array[i] = s.charAt(i) == '(' ? 1 : 0;
            }
            Node[] tree = new Node[4 * pow];
            build(tree, array, 1, 0, pow - 1);
            int k = Integer.parseInt(sc.readLine());
            StringBuilder ans = new StringBuilder();
            while (k > 0) {
                String[] temp = sc.readLine().split(" ");
                int left = Integer.parseInt(temp[0]);
                int right = Integer.parseInt(temp[1]);
                ans.append(get(tree, 1, 0, pow, left - 1, right).len).append("\n");
                k--;
            }
            System.out.println(ans);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}