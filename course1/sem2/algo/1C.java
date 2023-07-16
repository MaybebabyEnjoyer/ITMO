import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Task1C {

    private static class Node {
        long pref;
        long suf;

        long ans;

        boolean isZero;

        public Node(long pref, long suf, long ans, boolean isZero) {
            this.pref = pref;
            this.suf = suf;
            this.ans = ans;
            this.isZero = isZero;
        }
    }

    public static Node combine(Node leftChild, Node rightChild) {
        if (leftChild.ans == Integer.MIN_VALUE) {
            return rightChild;
        }
        if (rightChild.ans == Integer.MIN_VALUE) {
            return leftChild;
        }
        long newAns = Math.max(leftChild.ans, rightChild.ans);
        long newPref = leftChild.pref;
        long newSuf = rightChild.suf;
        if (leftChild.isZero) {
            if (rightChild.isZero) {
                return new Node(leftChild.pref + rightChild.pref, leftChild.pref + rightChild.pref,
                        leftChild.pref + rightChild.pref, true);
            } else {
                newPref = leftChild.pref + rightChild.pref;
            }
        } else if (rightChild.isZero) {
            newSuf = leftChild.suf + rightChild.pref;
        }
        if (leftChild.suf != 0 && rightChild.pref != 0) {
            newAns = Math.max(newAns, leftChild.suf + rightChild.pref);
        }
        return new Node(newPref, newSuf, newAns, false);
    }

    public static void build(Node[] tree, long[] a, int v, int l, int r) {
        if (l == r) {
            if (a[l] == 0) {
                tree[v] = new Node(1, 1, 1, true);
            } else {
                tree[v] = new Node(0, 0, 0, false);
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
            return new Node(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, false);
        }
        if (ql <= l && r <= qr) {
            return tree[v];
        }
        int m = (l + r) >> 1;
        Node leftChild = get(tree, 2 * v + 1, l, m, ql, qr);
        Node rightChild = get(tree, 2 * v + 2, m, r, ql, qr);
        return combine(leftChild, rightChild);
    }

    public static void set(Node[] tree, int v, int l, int r, int pos, int val) {
        if (r - l == 1) {
            if (val == 0) {
                tree[v] = new Node(1, 1, 1, true);
            } else {
                tree[v] = new Node(0, 0, 0, false);
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
                if (temp[0].equals("QUERY")) {
                    ans.append(get(tree, 1, 0, pow, Integer.parseInt(temp[1]) - 1, Integer.parseInt(temp[2])).ans).append("\n");
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