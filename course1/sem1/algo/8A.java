import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;

 public class E1 {
     static int maxGold(int[] arr, int n, int m) {
         Arrays.sort(arr);
         int[] dp = new int[m + 1];
         for (int i = 0; i < n; i++) {
             for (int j = m; j >= arr[i]; j--) {
                 dp[j] = Math.max(dp[j], dp[j - arr[i]] + arr[i]);
             }
         }
         return dp[m];
     }

     public static void main(String[] args) {
         Scanner in = new Scanner(System.in);
         int n = in.nextInt();
         int m = in.nextInt();
         int[] arr = new int[n];
         for (int i = 0; i < n; i++) {
             arr[i] = in.nextInt();
         }
         System.out.println(maxGold(arr, n, m));
     }
 }
 