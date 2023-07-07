import java.io.*;

import static java.lang.Integer.max;

public class H {
    static StreamTokenizer in;

    static int nextInt() throws IOException {
        in.nextToken();
        return (int) in.nval;
    }

    public static void main(String[] args) throws IOException {
        in = new StreamTokenizer(new BufferedReader(new InputStreamReader(System.in)));
        int n = nextInt();
        int max1 = - 1;
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = nextInt();
            max1 = max(max1, arr[i]);
            if (i > 0) {
                arr[i] += arr[i - 1];
            }
        }
        int[] answer = new int[(int) 1e7];
        int q = nextInt();
        for (int j = 0; j < q; j++) {
            int t = nextInt();
            if (t < max1) {
                System.out.println("Impossible");
                continue;
            }
            if (answer[t] > 0) {
                System.out.println(answer[t]);
                continue;
            }
            int curr = 0, p = -1, k = 0;
            while (p < n - 1) {
                int l = p + 1, r = n;
                while (r - l > 1) {
                    int m = (l + r) / 2;
                    if (arr[m] > curr + t) {
                        r = m;
                    }
                    else {
                        l = m;
                    }
                }
                k++;
                p = l;
                curr = arr[l];
            }
            answer[t] = k;
            System.out.println(k);
        }
    }
}
