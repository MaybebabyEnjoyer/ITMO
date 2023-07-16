import java.util.Scanner;

public class Task3F {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int k = sc.nextInt();
        int[] len = new int[n];
        for (int i = 0; i < n; i++) {
            len[i] = sc.nextInt();
        }
        int l = 1;
        int r = 10_000_001;
        while (l < r) {
            int m = (l + r) / 2;
            int counter = 0;
            for (int i = 0; i < n; i++) {
                counter += len[i] / m;
            }
            if (counter >= k) {
                l = m + 1;
            } else {
                r = m;
            }
        }
        System.out.println(l - 1);
    }
}