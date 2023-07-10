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
    public static int bisectR(int[] arr, int target) {
        int l = - 1;
        int r = arr.length;
        int mid;
        while (l < r - 1) {
            mid = (l + r) / 2;
            if (arr[mid] < target) l = mid;
            else r = mid;
        }
        return r + 1;
    }

    public static int bisectL(int[] arr, int target) {
        int l = -1;
        int r = arr.length;
        int mid;
        while (l < r - 1) {
            mid = (l + r) / 2;
            if (arr[mid] <= target) l = mid;
            else r = mid;
        }
        return l + 1;
    }
    public static boolean bisect(int[] arr, int target) {
        int l = -1;
        int r = arr.length - 1;
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
            int a = nextInt();
            if (bisect(n1, a)) {
                System.out.print(bisectR(n1, a));
                System.out.print(" ");
                System.out.print(bisectL(n1, a));
                System.out.println();
            }
            else System.out.println("0");
        }
    }
}