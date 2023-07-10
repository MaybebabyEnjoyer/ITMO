import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;

public class E1 {
    static StreamTokenizer in;

    static int nextInt() throws IOException {
        in.nextToken();
        return (int) in.nval;
    }
    public static void main(String[] args) throws IOException {
        in = new StreamTokenizer(new BufferedReader(new InputStreamReader(System.in)));
        int n = nextInt();
        int k = nextInt();
        int[] a = new int[n];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            a[i] = nextInt();
        }
        int[] qmin = new int[n];
        int lmin = 0, rmin = 0;
        for (int i = 0; i < n; i++) {
            while (lmin < rmin && a[qmin[rmin - 1]] >= a[i]) {
                rmin--;
            }
            qmin[rmin++] = i;
            while (lmin < rmin && qmin[lmin] <= i - k) {
                lmin++;
            }
            if (i >= k - 1) {
                sb.append(a[qmin[lmin]]).append(System.lineSeparator());
            }
        }
        System.out.println(sb.deleteCharAt(sb.length() - 1));
    }
}