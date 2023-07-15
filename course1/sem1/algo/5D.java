import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Task5D {
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            int m = Integer.parseInt(reader.readLine());
            String[] first = reader.readLine().split(" ");
            int n = Integer.parseInt(reader.readLine());
            String[] second = reader.readLine().split(" ");
            int[][] dp = new int[m + 1][n + 1];
            for (int i = 1; i < m + 1; i++) {
                for (int j = 1; j < n + 1; j++) {
                    if (Integer.parseInt(first[i - 1]) == Integer.parseInt(second[j - 1])) {
                        dp[i][j] = dp[i - 1][j - 1] + 1;
                    } else {
                        if (dp[i - 1][j] >= dp[i][j - 1]) {
                            dp[i][j] = dp[i - 1][j];
                        } else {
                            dp[i][j] = dp[i][j - 1];
                        }
                    }
                }
            }
            StringBuilder ans = new StringBuilder();
            int c1 = m;
            int c2 = n;
            while (c1 > 0 && c2 > 0) {
                int a = Integer.parseInt(first[c1 - 1]);
                int b = Integer.parseInt(second[c2 - 1]);
                if (a == b) {
                    ans.insert(0, a + " ");
                    c1--;
                    c2--;
                } else if (dp[c1 - 1][c2] == dp[c1][c2]) {
                    c1--;
                } else {
                    c2--;
                }
            }
            System.out.println(dp[m][n]);
            System.out.println(ans);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}