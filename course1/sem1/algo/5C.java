import java.util.Arrays;
import java.util.Scanner;

public class E1 {
    public static int[] findLIS(int[] sequence) {
        int[] dp = new int[sequence.length];
        int[] indices = new int[sequence.length];
        Arrays.fill(indices, -1);

        int maxLength = 0;
        int maxIndex = 0;

        for (int i = 0; i < sequence.length; i++) {
            dp[i] = 1;
            for (int j = 0; j < i; j++) {
                if (sequence[j] < sequence[i] && dp[j] + 1 > dp[i]) {
                    dp[i] = dp[j] + 1;
                    indices[i] = j;
                }
            }

            if (dp[i] > maxLength) {
                maxLength = dp[i];
                maxIndex = i;
            }
        }

        int[] LIS = new int[maxLength];
        int currentIndex = maxIndex;
        for (int i = maxLength - 1; i >= 0; i--) {
            LIS[i] = sequence[currentIndex];
            currentIndex = indices[currentIndex];
        }

        return LIS;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] sequence = new int[n];
        for (int i = 0; i < n; i++) {
            sequence[i] = sc.nextInt();
        }
        int[] LIS = findLIS(sequence);
        System.out.println(LIS.length);
        System.out.println(Arrays.toString(LIS).replaceAll("[\\[\\],]", ""));
    }
}