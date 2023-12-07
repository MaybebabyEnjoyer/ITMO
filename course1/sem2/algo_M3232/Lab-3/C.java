import java.util.Scanner;
import java.util.ArrayList;

public class C {
    private static class Node {
        private final int i;
        private final int y;

        public Node(final int i, final int y) {
            this.i = i;
            this.y = y;
        }
    }

    static int timer;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int l = (int)Math.ceil(Math.log(n + 1) / Math.log(2));
        ArrayList<Node>[] tree = new ArrayList[n + 1];
        for (int i = 0; i < n + 1; i++) {
            tree[i] = new ArrayList<>();
        }
        int[] t_in = new int[n + 1];
        int[] t_out = new int[n + 1];
        Node[][] up = new Node[n + 1][l + 1];
        for (int i = 2; i < n + 1; i++) {
            int x = sc.nextInt();
            int y = sc.nextInt();
            tree[x].add(new Node(i, y));
        }
        dfs(1, 1, Integer.MAX_VALUE, l, t_in, t_out, tree, up);
        int m = sc.nextInt();
        for (int i = 0; i < m; i++) {
            int u = sc.nextInt();
            int v = sc.nextInt();
            System.out.println(minEdge(u, v, l, t_in, t_out, up));
        }
    }

    static boolean upper(int u, int v, int[] t_in, int[] t_out) {
        return t_in[u] <= t_in[v] && t_out[u] >= t_out[v];
    }

    static void dfs(int v, int p1, int p2,  int l, int[] t_in, int[] t_out, ArrayList<Node>[] tree, Node[][] up) {
        t_in[v] = timer++;
        up[v][0] = new Node(p1, p2);
        for (int i = 1; i <= l; i++) {
            up[v][i] = new Node(up[up[v][i - 1].i][i - 1].i, Math.min(up[up[v][i - 1].i][i - 1].y, up[v][i - 1].y));
        }
        for (int i = 0; i < tree[v].size(); i++) {
            int to = tree[v].get(i).i;
            if (to != p1) {
                dfs(to, v, tree[v].get(i).y, l, t_in, t_out, tree, up);
            }
        }
        t_out[v] = timer++;
    }

    static int minEdge(int u, int v, int l, int[] t_in, int[] t_out, Node[][] up) {
        return Math.min(lcaPlus(u, v, l, t_in, t_out, up), lcaPlus(v, u, l, t_in, t_out, up));
    }

    static int lcaPlus(int u, int v, int l, int[] t_in, int[] t_out, Node[][] up) {
        int result = Integer.MAX_VALUE;
        for (int i = l; i >= 0; i--) {
            if (!upper(up[u][i].i, v, t_in, t_out)) {
                result = Math.min(up[u][i].y, result);
                u = up[u][i].i;
            }
        }
        return !upper(u, v, t_in, t_out) ? Math.min(up[u][0].y, result) : result;
    }
}
