import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class E1 {
    public static int knapsack(int[] weights, int[] values, int capacity) {
        int n = weights.length;
        int[][] dp = new int[n + 1][capacity + 1];
        int[][] items = new int[n + 1][capacity + 1];

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= capacity; j++) {
                if (weights[i - 1] <= j) {
                    if (dp[i - 1][j] > dp[i - 1][j - weights[i - 1]] + values[i - 1]) {
                        dp[i][j] = dp[i - 1][j];
                        items[i][j] = items[i - 1][j];
                    } else {
                        dp[i][j] = dp[i - 1][j - weights[i - 1]] + values[i - 1];
                        items[i][j] = i;
                    }
                } else {
                    dp[i][j] = dp[i - 1][j];
                    items[i][j] = items[i - 1][j];
                }
            }
        }
        
        int count = 0;
        int i = n, j = capacity;
        while (i > 0 && j > 0) {
            if (items[i][j] != items[i - 1][j]) {
                count++;
                j -= weights[i - 1];
            }
            i--;
        }
        System.out.println(count);

        List<Integer> knapsack = new ArrayList<>();
        i = n;
        j = capacity;
        while (i > 0 && j > 0) {
            if (items[i][j] != items[i - 1][j]) {
                knapsack.add(items[i][j]);
                j -= weights[i - 1];
            }
            i--;
        }
        StringBuilder sb = new StringBuilder();
        for (int k = knapsack.size() - 1; k >= 0; k--) {
            sb.append(knapsack.get(k)).append(" ");
        }
        System.out.println(sb.toString().trim());
        return dp[n][capacity];
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int capacity = sc.nextInt();
        int[] weights = new int[n];
        int[] values = new int[n];
        for (int i = 0; i < n; i++) {
            weights[i] = sc.nextInt();
        }
        for (int i = 0; i < n; i++) {
            values[i] = sc.nextInt();
        }
        knapsack(weights, values, capacity);
    }
}