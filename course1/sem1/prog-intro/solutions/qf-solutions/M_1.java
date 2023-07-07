import java.io.BufferedReader;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class M {
    static StreamTokenizer in;

    static int nextInt() throws IOException {
        in.nextToken();
        return (int) in.nval;
    }

    public static void main(String[] args) throws IOException {
        in = new StreamTokenizer(new BufferedReader(new InputStreamReader(System.in)));
        int t = nextInt();
        for (int i = 0; i < t; i++) {
            calc();
        }
    }

    static void calc() throws IOException {
        int n = nextInt(), res = 0;
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = nextInt();
        }
        Map<Integer, Integer> map = new HashMap<>();
        for (int j = n - 1; j > 0; j--) {
            for (int i = 0; i < j; i++) {
                if (map.containsKey(2 * arr[j] - arr[i])) {
                    res += map.get(2 * arr[j] - arr[i]);
                }
            }
            map.merge(arr[j], 1, Integer::sum);
        }
        System.out.println(res);
    }
}