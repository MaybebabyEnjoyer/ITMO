import java.io.*;
import java.nio.charset.StandardCharsets;

public class E {
    static int l, nth = 1;
    static int[] depth, parent;
    static int[][] dynamics;

    public static void main(String[] args) {
        try {
            MyScanner sc = new MyScanner(System.in);
            try {
                int m = sc.nextInt();
                l = (int)Math.ceil(Math.log(m + 1) / Math.log(2));
                depth = new int[m + 1];
                parent = new int[m + 1];
                dynamics = new int[m + 1][l + 1];
                pre(1);
                for (int i = 0; i < m; i++) {
                    String operation = sc.next();
                    switch (operation) {
                        case "+" -> {
                            int v = sc.nextInt();
                            insert(v);
                        }
                        case "-" -> {
                            int v = sc.nextInt();
                            delete(v);
                        }
                        case "?" -> {
                            int u = sc.nextInt();
                            int v = sc.nextInt();
                            System.out.println(lca(u, v));
                        }
                    }
                }
            } finally {
                sc.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    static void pre(int v) {
        depth[v] = v;
        parent[v] = v;
        dynamics[v][0] = v;
        for (int i = 1; i <= l; i++) {
            dynamics[v][i] = dynamics[dynamics[v][i - 1]][i - 1];
        }
    }

    static void insert(int v) {
        int v1 = ++nth;
        depth[v1] = depth[v] + 1;
        parent[v1] = v1;
        dynamics[v1][0] = v;
        for (int i = 1; i <= l; i++) {
            dynamics[v1][i] = dynamics[dynamics[v1][i - 1]][i - 1];
        }
    }

    static int find(int v) {
        if (parent[v] == v) {
            return v;
        }
        parent[v] = find(parent[v]);
        return parent[v];
    }

    static void union(int x, int y) {
        x = find(x);
        y = find(y);
        if (x != y) {
            parent[x] = y;
        }
    }

    static void delete(int v) {
        union(v, dynamics[v][0]);
    }

    static int lca(int u, int v) {
        if (depth[u] < depth[v]) {
            int temp = u;
            u = v;
            v = temp;
        }
        for (int i = l; i >= 0; i--) {
            if (depth[dynamics[u][i]] - depth[v] >= 0) {
                u = dynamics[u][i];
            }
        }
        if (u == v) {
            return find(u);
        }
        for (int i = l; i >= 0; i--) {
            if (dynamics[u][i] != dynamics[v][i]) {
                u= dynamics[u][i];
                v = dynamics[v][i];
            }
        }
        return find(dynamics[v][0]);
    }
}

class MyScanner {
    private final Reader reader;

    public MyScanner(InputStream stream) throws IOException {
        reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
    }
    
    public void close() throws IOException {
        reader.close();
    }
    
    private int j;
    
    public String next() throws IOException {
        StringBuilder str = new StringBuilder();
        int i = j;
        while (i == 0 || Character.isWhitespace((char) i)) {
            i = reader.read();
        }
        while (i != 0 && !Character.isWhitespace((char) i)) {
            str.append((char) i);
            i = reader.read();
            if (i == -1) {
                return str.toString();
            }
        }
        return str.toString();
    }

    public int nextInt() throws IOException {
        StringBuilder strInt = new StringBuilder();
        int i = j;
        while (!Character.isDigit((char) i) && (char) i != '-') {
            i = reader.read();
        }
        while (Character.isDigit((char) i) || (char) i == '-') {
            strInt.append((char) i);
            i = reader.read();
            if (i == -1) {
                return Integer.parseInt(strInt.toString());
            }
        }
        return Integer.parseInt(strInt.toString());
    }
}
