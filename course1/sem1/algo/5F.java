import java.util.Scanner;

public class Task5F {

    public static int binarySearch(int[] a, int k) {
        if (k > a[a.length - 1] || k < a[0]) {
            return -1;
        }
        k++;
        int l = -1;
        int r = a.length;
        while (l < r - 1) {
            int m = (l + r) / 2;
            if (a[m] == k) {
                return m;
            }
            if (a[m] < k) {
                l = m;
            } else {
                r = m;
            }
        }
        return r;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] arr = new int[n];
        for (int i = n - 1; i >= 0; i--) {
            arr[i] = sc.nextInt();
        }
        int[] dp = new int[n + 1];
        int[] pos = new int[n + 1];
        int[] prev = new int[n];
        int l = 0;
        pos[0] = -1;
        dp[0] = Integer.MIN_VALUE;
        for (int i = 1; i < n + 1; i++) {
            dp[i] = Integer.MAX_VALUE;
        }
        for (int i = 0; i < n; i++) {
            int curr = arr[i];
            int ind = binarySearch(dp, curr);
            if (dp[ind - 1] <= curr && curr <= dp[ind]) {
                dp[ind] = curr;
                pos[ind] = i;
                prev[i] = pos[ind - 1];
                l = Math.max(l, ind);
            }
        }

        StringBuilder ans = new StringBuilder();
        int num = pos[l];
        int count = 0;
        while (num != -1) {
            ans.append(n - num).append(" ");
            count++;
            num = prev[num];
        }
        System.out.println(count);
        System.out.println(ans);
    }
}