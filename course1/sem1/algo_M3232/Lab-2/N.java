import java.util.*;

public class N {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String str1 = sc.next();
		int m = str1.length();
		String str2 = sc.next();
		int n = str2.length();
		int[][] dp = new int[m + 1][n + 1];
		dp[0][0] = 0;
		for (int i = 1; i < m + 1; i++) {
			dp[i][0] = i;
		}
		for (int j = 1; j < n + 1; j++) {
			dp[0][j] = j;
		}
		for (int i = 1; i < m + 1; i++) {
			for (int j = 1; j < n + 1; j++) {
				if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
					dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1]);
				} else {
					dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + 1);
				}
			}
		}
		System.out.println(dp[m][n]);
	}
}
