import java.io.IOException;
import java.util.Scanner;

public class J {

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[][] arr = new int[n][n];
        sc.nextLine();
        for (int i = 0; i < n; i++) {
            String s = sc.nextLine();
            for (int j = 0; j < n; j++) {
                arr[i][j] = s.charAt(j) - (int) '0';
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (arr[i][j] != 0) {
                    for (int k = j + 1; k < n; k++) {
                        arr[i][k] -= arr[j][k];
                        if (arr[i][k] < 0) {
                            arr[i][k] += 10;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(arr[i][j]);
            }
            System.out.println();
        }
    }
}