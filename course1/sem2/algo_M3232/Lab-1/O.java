import java.util.Scanner;

public class O {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[][][] fenwick = new int[n][n][n];
        int operation;
        int x, y, z, value;
        int x1, y1, z1, x2, y2, z2;
        label:
        while (sc.hasNext()) {
            operation = sc.nextInt();
            switch (operation) {
                case 1:
                    x = sc.nextInt();
                    y = sc.nextInt();
                    z = sc.nextInt();
                    value = sc.nextInt();
                    add(x, y, z, value, n, fenwick);
                    break;
                case 2:
                    x1 = sc.nextInt();
                    y1 = sc.nextInt();
                    z1 = sc.nextInt();
                    x2 = sc.nextInt();
                    y2 = sc.nextInt();
                    z2 = sc.nextInt();
                    System.out.println(count(x1, y1, z1, x2, y2, z2, fenwick));
                    break;
                case 3:
                    break label;
            }
        }
    }

    static void add(int x, int y, int z, int value, int n, int[][][] fenwick) {
        for (int i = x; i < n; i = (i | (i + 1))) {
            for (int j = y; j < n; j = (j | (j + 1))) {
                for (int k = z; k < n; k = (k | (k + 1))) {
                    fenwick[i][j][k] += value;
                }
            }
        }
    }

    static int sum(int x, int y, int z, int[][][] fenwick) {
        int result = 0;
        for (int i = x; i >= 0; i = (i & (i + 1)) - 1) {
            for (int j = y; j >= 0; j = (j & (j + 1)) - 1) {
                for (int k = z; k >= 0; k = (k & (k + 1)) - 1) {
                    result += fenwick[i][j][k];
                }
            }
        }
        return result;
    }

    static int count(int x1, int y1, int z1, int x2, int y2, int z2, int[][][] fenwick) {
        int value1 = sum(x2, y2, z2, fenwick) - sum(x1 - 1, y2, z2, fenwick) - sum(x2, y1 - 1, z2, fenwick) + sum(x1 - 1, y1 - 1, z2, fenwick);
        int value2 = sum(x2, y2, z1 - 1, fenwick) - sum(x1 - 1, y2, z1 - 1, fenwick) - sum(x2, y1 - 1, z1 - 1, fenwick) + sum(x1 - 1, y1 - 1, z1 - 1, fenwick);
        return value1 - value2;
    }
}
