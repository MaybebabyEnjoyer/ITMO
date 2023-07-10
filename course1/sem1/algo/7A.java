import java.util.*;

public class E1 {
    static int[] a;
    static int[] b;
    static int[] exp;

    static int find(int x) {
        if (x == a[x]) {
            return x;
        } else {
            return find(a[x]);
        }
    }

    static void union(int x, int y) {
        x = find(x);
        y = find(y);
        if (x == y) {
            return;
        }

        if (b[x] < b[y]) {
            int temp = x;
            x = y;
            y = temp;
        }
        a[y] = x;
        exp[y] -= exp[x];

        if (b[x] == b[y]) {
            b[x] += 1;
        }
    }

    static int countExp(int x) {
        if (x == a[x]) {
            return exp[x];
        } else {
            return countExp(a[x]) + exp[x];
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int m = sc.nextInt();
        b = new int[n + 1];
        a = new int[n + 1];
        exp = new int[n + 1];
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i <= n; i++) {
            a[i] = i;
        }

        for (int i = 0; i < m; i++) {
            String operation = sc.next();
            if (operation.equals("add")) {
                int x = sc.nextInt();
                int v = sc.nextInt();
                exp[find(x)] += v;
            } else if (operation.equals("join")) {
                int x = sc.nextInt();
                int y = sc.nextInt();
                union(x, y);
            } else {
                int x = sc.nextInt();
                sb.append(countExp(x)).append(System.lineSeparator());
            }
        }
        System.out.println(sb.deleteCharAt(sb.length() - 1));
    }
}