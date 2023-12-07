import java.util.*;

public class K {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		int m = sc.nextInt();
		int[][] matrix = new int[n][m];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				matrix[i][j] = sc.nextInt();
			}
		}
		int[][] dynamics = new int[n][m];
		dynamics[0][0] = matrix[0][0];
		for (int j = 1; j < m; j++) {
			dynamics[0][j] = matrix[0][j] + dynamics[0][j - 1];
		}
		for (int i = 1; i < n; i++) {
			dynamics[i][0] = matrix[i][0] + dynamics[i - 1][0];
		}
		for (int i = 1; i < n; i++) {
			for (int j = 1; j < m; j++) {
				dynamics[i][j] = Math.max(dynamics[i][j - 1], dynamics[i - 1][j]) + matrix[i][j];
			}
		}
		StringBuilder result = new StringBuilder();
		int i = n - 1;
		int j = m - 1; 
		while (i != 0 || j != 0) {
			if (i == 0) {
				j--;
	            result.append('R');
			} else if (j == 0) {
				i--;
				result.append('D');
			} else {
				if (dynamics[i - 1][j] > dynamics[i][j - 1]) {
					i--;
					result.append('D');
				}
		        else {
		        	j--;
		        	result.append('R');
		        }
			}
		}
		System.out.println(dynamics[n - 1][m - 1]);
		System.out.println(result.reverse().toString());
    }
}
