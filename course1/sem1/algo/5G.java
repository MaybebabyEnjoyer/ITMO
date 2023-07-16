import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Task5G {
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String s = reader.readLine();
            int n = Integer.parseInt(reader.readLine());
            long[][] dp = new long[n][26];
            dp[s.length() - 1][s.charAt(s.length() - 1) - 97] = 1;
            boolean flag = true;
            for (int i = 0; i < s.length() - 1; i++) {
                if (s.charAt(i) + 1 == s.charAt(i + 1)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                for (int i = s.length(); i < n; i++) {
                    for (int j = 0; j < 26; j++) {
                        long sum = 0;
                        for (int k = 0; k < 26; k++) {
                            if (j != k + 1) {
                                sum += dp[i - 1][k] % 998244353;
                            }
                        }
                        dp[i][j] = sum;
                    }
                }
                long ans = 0;
                for (int j = 0; j < 26; j++) {
                    ans += dp[n - 1][j];
                }
                System.out.println(ans % 998244353);
            } else {
                System.out.println(0);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}