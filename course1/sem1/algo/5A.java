import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Task5A {
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            int n = Integer.parseInt(reader.readLine());
            StringBuilder line = new StringBuilder(reader.readLine());
            int[] dp = new int[n];
            dp[0] = 0;
            for (int i = 1; i < n; i++) {
                char kletka = line.charAt(i);
                int first = dp[i - 1];
                int second = (i - 3 >= 0) ? dp[i - 3] : -1;
                int third = (i - 5 >= 0) ? dp[i - 5] : -1;
                int max = Math.max(third, Math.max(first, second));
                if (kletka == '.') {
                    dp[i] = max;
                } else if (kletka == 'w') {
                    dp[i] = -1;
                } else if (kletka == '"') {
                    if ((i - 1 < 0 || dp[i - 1] == -1) && (i - 3 < 0 || dp[i - 3] == -1) && (i - 5 < 0 || dp[i - 5] == -1)) {
                        dp[i] = -1;
                    } else {
                        dp[i] = max + 1;
                    }
                }
            }
            System.out.println(dp[n - 1]);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}