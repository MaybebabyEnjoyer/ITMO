import java.util.Scanner;
import java.util.ArrayList;

public class B {
    static int timer;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int l = (int)Math.ceil(Math.log(n + 1) / Math.log(2));
        ArrayList<Integer>[] tree = new ArrayList[n + 1];
        for (int i = 0; i < n + 1; i++) {
            tree[i] = new ArrayList<>();
        }
        int[] t_in = new int[n + 1];
        int[] t_out = new int[n + 1];
        int[][] up = new int[n + 1][l + 1];
        for (int i = 2; i < n + 1; i++) {
            int x = sc.nextInt();
            tree[x].add(i);
        }
        dfs(1, 1, l, t_in, t_out, tree, up);
        int m = sc.nextInt();
        for (int i = 0; i < m; i++) {
            int u = sc.nextInt();
            int v = sc.nextInt();
            System.out.println(lca(u, v, l, t_in, t_out, up));
        }
    }

    static boolean upper(int u, int v, int[] t_in, int[] t_out) {
        return t_in[u] <= t_in[v] && t_out[u] >= t_out[v];
    }

    static void dfs(int v, int p, int l, int[] t_in, int[] t_out, ArrayList<Integer>[] tree, int[][] up) {
        t_in[v] = timer++;
        up[v][0] = p;
        for (int i = 1; i <= l; i++) {
            up[v][i] = up[up[v][i - 1]][i - 1];
        }
        for (int i = 0; i < tree[v].size(); i++) {
            int to = tree[v].get(i);
            if (to != p)
                dfs (to, v, l, t_in, t_out, tree, up);
        }
        t_out[v] = timer++;
    }

    static int lca(int u, int v, int l, int[] t_in, int[] t_out, int[][] up) {
        if (upper(u, v, t_in, t_out)) {
            return u;
        }
        if (upper(v, u, t_in, t_out)) {
            return v;
        }
        for (int i = l; i >= 0; i--) {
            if (!upper(up[u][i], v, t_in, t_out)) {
                u = up[u][i];
            }
        }
        return up[u][0];
    }
}
