import java.io.*;
import java.nio.charset.StandardCharsets;

public class A {
    public static void main(String[] args) {
        try {
            MyScanner sc = new MyScanner(System.in);
            try {
                int n = sc.nextInt();
                int m = sc.nextInt();
                int[] array = new int[n];
                array[0] = sc.nextInt();
                for (int i = 1; i < n; i++) {
                    array[i] = (23 * array[i - 1] + 21563) % 16714589;
                }
                int[][] sparseTable = new int[n][((int) (Math.log(n) / Math.log(2))) + 2];
                build(array, sparseTable, n);
                int u = sc.nextInt();
                int v = sc.nextInt();
                int r = min(Math.min(u, v) - 1, Math.max(u, v) - 1, sparseTable);
                for (int i = 1; i < m; i++) {
                    u = ((17 * u + 751 + r + 2 * i) % n) + 1;
                    v = ((13 * v + 593 + r + 5 * i) % n) + 1;
                    r = min(Math.min(u - 1, v - 1), Math.max(u - 1, v - 1), sparseTable);
                }
                System.out.println(u + " " + v + " " + r);
            } finally {
                sc.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    static void build(int[] array, int[][] sparseTable, int n) {
        for (int i = 0; i < n; i++) {
            sparseTable[i][0] = array[i];
        }
        for (int i = 1; (1 << i) <= n; i++) {
            for (int j = 0; (j + (1 << i) - 1) < n; j++) {
                sparseTable[j][i] = Math.min(sparseTable[j][i - 1], sparseTable[j + (1 << (i - 1))][i - 1]);
            }
        }
    }

    static int min(int left, int right, int[][] sparseTable) {
        int index = (int) (Math.log(right - left + 1) / Math.log(2));
        return Math.min(sparseTable[left][index], sparseTable[right - (1 << index) + 1][index]);
    }
}

class MyScanner {
    // Reader Block (Start)
    private final Reader reader;

    public MyScanner(InputStream stream) throws IOException {
        reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
    }

    public void close() throws IOException {
        reader.close();
    }
// Reader Block (End)

    // Methods Block (Start)
    private int j;

    public boolean hasNext() throws IOException {
        int i = reader.read();
        while (Character.isWhitespace((char) i) && i != -1) {
            i = reader.read();
        }
        j = i;
        return !Character.isWhitespace((char) i) && i != -1;
    }

    public String next() throws IOException {
        StringBuilder str = new StringBuilder();
        int i = j;
        while (Character.isWhitespace((char) i)) {
            i = reader.read();
        }
        while (!Character.isWhitespace((char) i)) {
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

    public long nextLong() throws IOException {
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
        return Long.parseLong(strInt.toString());
    }
// Methods Block (End)
}
