import java.io.BufferedReader;
import java.io.*;

import static java.lang.Integer.max;
import static java.lang.Math.min;

public class I {
    static StreamTokenizer in;

    static int nextInt() throws IOException {
        in.nextToken();
        return (int)in.nval;
    }

    public static void main(String[] args) throws IOException {
        in = new StreamTokenizer(new BufferedReader(new InputStreamReader(System.in)));
        int n = nextInt();
        int xLeft = (int) 1e9;
        int xRight = (int) -1e9;
        int yLeft = (int) 1e9;
        int yRight = (int) -1e9;
        for (int i = 0; i < n; i++) {
            int x = nextInt(), y = nextInt(), h = nextInt();
            xLeft = min(x - h, xLeft);
            xRight = max(x + h, xRight);
            yLeft = min(y - h, yLeft);
            yRight = max(y + h, yRight);
        }
        System.out.println((xLeft + xRight) / 2 + " " + (yLeft + yRight) / 2 + " " + max(xRight - xLeft + 1, yRight - yLeft + 1) / 2);
    }
}