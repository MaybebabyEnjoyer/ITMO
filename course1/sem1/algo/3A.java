import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.*;
import java.io.*;

public class svmsvsv {
    static StreamTokenizer in;

    static int nextInt() throws IOException {
        in.nextToken();
        return (int) in.nval;
    }
    public static boolean bisect(int[] arr, int target, int left, int right) {
        int l = left;
        int r = right;
        int mid;
        while (l < r - 1) {
            mid = (l + r) / 2;
            if (arr[mid] == target) return true;
            if (arr[mid] < target) l = mid;
            else r = mid;
            if (arr[r] == target) return true;
        }
        return arr[r] == target;
    }

    public static void main(String[] args) throws IOException {
        in = new StreamTokenizer(new BufferedReader(new InputStreamReader(System.in)));
        int n = nextInt();
        int k = nextInt();
        int[] n1 = new int[n];
        for (int i = 0; i < n; i++) {
            n1[i] = nextInt();
        }
        for (int i = 0; i < k; i++) {
            System.out.println(bisect(n1, nextInt(), -1, n1.length - 1) ? "YES" : "NO");
        }
    }
}