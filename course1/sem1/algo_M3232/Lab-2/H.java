import java.util.Scanner;

public class H {
    private static int find(int element, int[] parents) {
        while (parents[element] != element) {
            element = parents[element];
        }
        return element;
    }

    private static void join(int x, int y, int[] parents, int[] points, int[] ranks) {
        x = find(x, parents);
        y = find(y, parents);
        if (x == y) {
            return;
        }
        if (ranks[x] > ranks[y]) {
            int swap = x;
            x = y;
            y = swap;
        }
        if (ranks[x] == ranks[y]) {
            ranks[y]++;
        }
        parents[x] = y;
        points[x] -= points[y];
    }

    private static void add(int x, int v, int[] parents, int[] points) {
        x = find(x, parents);
        points[x] += v;
    }

    private static int get(int x, int[] parents, int[] points) {
        int result = 0;
        while (parents[x] != x) {
            result += points[x];
            x = parents[x];
        }
        result += points[x];
        return result;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] parents = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            parents[i] = i;
        }
        int[] points = new int[n + 1];
        int[] ranks = new int[n + 1];
        int m = sc.nextInt();
        String operation;
        int x, y, v;
        for (int i = 0; i < m; i++) {
            operation = sc.next();
            switch (operation) {
                case "join" -> {
                    x = sc.nextInt();
                    y = sc.nextInt();
                    join(x, y, parents, points, ranks);
                }
                case "add" -> {
                    x = sc.nextInt();
                    v = sc.nextInt();
                    add(x, v, parents, points);
                }
                case "get" -> {
                    x = sc.nextInt();
                    System.out.println(get(x, parents, points));
                }
            }
        }
    }
}
