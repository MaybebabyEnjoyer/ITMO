import java.io.*;
import java.nio.charset.StandardCharsets;

public class D {
    public static void main(String[] args) {
        try {
            MyScanner sc = new MyScanner(System.in);
            try {
                int n = sc.nextInt();
                long[] array = new long[n];
                for (int i = 0; i < n; i++) {
                    array[i] = sc.nextLong();
                }
                long[] value = new long[4 * n];
                long[] flagAdd = new long[4 * n];
                Long[] flagSet = new Long[4 * n];
                build(array, value, 0, 0, n);
                String operation;
                int i, j;
                long x;
                while (sc.hasNext()) {
                    operation = sc.next();
                    i = sc.nextInt();
                    j = sc.nextInt();
                    if (operation.equals("set")) {
                        x = sc.nextLong();
                        set(i - 1, j, x, 0, 0, n, value, flagAdd, flagSet);
                    } else if (operation.equals("add")) {
                        x = sc.nextInt();
                        add(i - 1, j, x, 0, 0, n, value, flagAdd, flagSet);
                    } else {
                        System.out.println(min(i - 1, j, 0, 0, n, value, flagAdd, flagSet));
                    }
                }
            } finally {
                sc.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    static void build(long[] array, long[] value, int v, int vl, int vr) {
        if (vl + 1 == vr) {
            value[v] = array[vl];
            return;
        }
        int vm = vl + (vr - vl) / 2;
        build(array, value, 2 * v + 1, vl, vm);
        build(array, value, 2 * v + 2, vm, vr);
        value[v] = Math.min(value[2 * v + 1], value[2 * v + 2]);
    }

    static void pushAdd(int v, int vl, int vr, long[] value, long[] flagAdd, Long[] flagSet) {
        if (flagAdd[v] != 0) {
            if (vl + 1 == vr) {
                value[v] += flagAdd[v];
                pushSet(v, vl, vr, value, flagAdd, flagSet);
                flagAdd[v] = 0;
            } else {
                value[v] += flagAdd[v];
                int vm = vl + (vr - vl) / 2;
                pushSet(2 * v + 1, vl, vm, value, flagAdd, flagSet);
                pushSet(2 * v + 2, vm, vr, value, flagAdd, flagSet);
                flagAdd[2 * v + 1] += flagAdd[v];
                flagAdd[2 * v + 2] += flagAdd[v];
                flagAdd[v] = 0;
            }
        }
    }

    static void add(int l, int r, long x, int v, int vl, int vr, long[] value, long[] flagAdd, Long[] flagSet) {
        pushAdd(v, vl, vr, value, flagAdd, flagSet);
        pushSet(v, vl, vr, value, flagAdd, flagSet);
        if (l >= vr || vl >= r) {
            return;
        }
        if (vl >= l && r >= vr) {
            flagAdd[v] += x;
            pushAdd(v, vl, vr, value, flagAdd, flagSet);
            pushSet(v, vl, vr, value, flagAdd, flagSet);
            return;
        }
        int vm = vl + (vr - vl) / 2;
        add(l, r, x, 2 * v + 1, vl, vm, value, flagAdd, flagSet);
        add(l, r, x, 2 * v + 2, vm, vr, value, flagAdd, flagSet);
        value[v] = Math.min(value[2 * v + 1], value[2 * v + 2]);
    }

    static void pushSet(int v, int vl, int vr, long[] value, long[] flagAdd, Long[] flagSet) {
        if (flagSet[v] != null) {
            if (vl + 1 == vr) {
                value[v] = flagSet[v];
                flagSet[v] = null;
            } else {
                value[v] = flagSet[v];
                flagAdd[2 * v + 1] = 0;
                flagAdd[2 * v + 2] = 0;
                flagSet[2 * v + 1] = flagSet[v];
                flagSet[2 * v + 2] = flagSet[v];
                flagSet[v] = null;
            }
        }
    }

    static void set(int l, int r, long x, int v, int vl, int vr, long[] value, long[] flagAdd, Long[] flagSet) {
        pushAdd(v, vl, vr, value, flagAdd, flagSet);
        pushSet(v, vl, vr, value, flagAdd, flagSet);
        if (l >= vr || vl >= r) {
            return;
        }
        if (vl >= l && r >= vr) {
            flagSet[v] = x;
            pushAdd(v, vl, vr, value, flagAdd, flagSet);
            pushSet(v, vl, vr, value, flagAdd, flagSet);
            return;
        }
        int vm = vl + (vr - vl) / 2;
        set(l, r, x, 2 * v + 1, vl, vm, value, flagAdd, flagSet);
        set(l, r, x, 2 * v + 2, vm, vr, value, flagAdd, flagSet);
        value[v] = Math.min(value[2 * v + 1], value[2 * v + 2]);
    }

    static long min(int l, int r, int v, int vl, int vr, long[] value, long[] flagAdd, Long[] flagSet) {
        pushAdd(v, vl, vr, value, flagAdd, flagSet);
        pushSet(v, vl, vr, value, flagAdd, flagSet);
        if (l >= vr || vl >= r) {
            return Long.MAX_VALUE;
        }
        if (vl >= l && r >= vr) {
            return value[v];
        }
        int vm = vl + (vr - vl) / 2;
        long min1 = min(l, r, 2 * v + 1, vl, vm, value, flagAdd, flagSet);
        long min2 = min(l, r, 2 * v + 2, vm, vr, value, flagAdd, flagSet);
        return Math.min(min1, min2);
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