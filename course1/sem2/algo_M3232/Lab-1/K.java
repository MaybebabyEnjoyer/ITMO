import java.io.*;
import java.nio.charset.StandardCharsets;

public class K {
    private static class Matrix {
        private final int a_11;
        private final int a_12;
        private final int a_21;
        private final int a_22;

        public Matrix(int a_11, int a_12, int a_21, int a_22) {
            this.a_11 = a_11;
            this.a_12 = a_12;
            this.a_21 = a_21;
            this.a_22 = a_22;
        }
    }

    public static void main(String[] args) {
        File input = new File("crypto.in");
        File output = new File("crypto.out");
        try {
            MyScanner sc = new MyScanner(input);
            try {
                try {
                    try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), StandardCharsets.UTF_8))) {
                        int r = sc.nextInt();
                        int n = sc.nextInt();
                        Matrix[] array = new Matrix[n];
                        Matrix[] tree = new Matrix[4 * n];
                        int m = sc.nextInt();
                        for (int i = 0; i < n; i++) {
                            array[i] = new Matrix(sc.nextInt(), sc.nextInt(), sc.nextInt(), sc.nextInt());
                        }
                        build(array, tree, 0, 0, n, r);
                        int left, right;
                        Matrix answer;
                        for (int i = 0; i < m; i++) {
                            left = sc.nextInt() - 1;
                            right = sc.nextInt();
                            answer = get(left, right, 0, 0, n, tree, r);
                            bw.write(answer.a_11 + " " + answer.a_12);
                            bw.newLine();
                            bw.write(answer.a_21 + " " + answer.a_22);
                            bw.newLine();
                            bw.newLine();
                        }
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            } finally {
                sc.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    static void build(Matrix[] array, Matrix[] tree, int v, int vl, int vr, int r) {
        if (vl + 1 == vr) {
            tree[v] = array[vl];
            return;
        }
        int vm = vl + (vr - vl) / 2;
        build(array, tree, 2 * v + 1, vl, vm, r);
        build(array, tree, 2 * v + 2, vm, vr, r);
        tree[v] = multiplication(tree[2 * v + 1], tree[2 * v + 2], r);
    }

    static Matrix multiplication(Matrix matrix1, Matrix matrix2, int r) {
        return new Matrix((matrix1.a_11 * matrix2.a_11 + matrix1.a_12 * matrix2.a_21) % r,
                (matrix1.a_11 * matrix2.a_12 + matrix1.a_12 * matrix2.a_22) % r,
                (matrix1.a_21 * matrix2.a_11 + matrix1.a_22 * matrix2.a_21) % r,
                (matrix1.a_21 * matrix2.a_12 + matrix1.a_22 * matrix2.a_22) % r);
    }

    static Matrix get(int left, int right, int v, int vl, int vr, Matrix[] tree, int r) {
        if (left >= vr || vl >= right) {
            return new Matrix(1, 0, 0, 1);
        }
        if (vl >= left && right >= vr) {
            return tree[v];
        }
        int vm = vl + (vr - vl) / 2;
        Matrix matrix1 = get(left, right, 2 * v + 1, vl, vm, tree, r);
        Matrix matrix2 = get(left, right, 2 * v + 2, vm, vr, tree, r);
        return multiplication(matrix1, matrix2, r);
    }
}

class MyScanner {
    // Reader Block (Start)
    private final Reader reader;

    public MyScanner(InputStream stream) throws IOException {
        reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
    }

    public MyScanner(File file) throws FileNotFoundException, IOException {
        reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
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
            if (i == -1 || i == '\n' || i == '\r') {
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
            if (i == -1 || i == '\n' || i == '\r') {
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
