import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Task1H {
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String[] tmp = reader.readLine().split(" ");
            int n = Integer.parseInt(tmp[0]);
            int m = Integer.parseInt(tmp[1]);
            int k = Integer.parseInt(tmp[2]);
            String[] lines = new String[n];
            for (int i = 0; i < n; i++) {
                lines[i] = reader.readLine();
            }
            lines = sort(lines, n, m, k);
            for (String line : lines) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String[] sort(String[] lines, int n, int m, int k) {
        for (int i = 1; i < k + 1; i++) {
            int[] cnt = new int[123];
            for (int j = 0; j < n; j++) {
                cnt[lines[j].charAt(lines[j].length() - i)]++;
            }
            int count = 0;
            int temp;
            for (int j = 97; j < 'z' + 1; j++) {
                temp = cnt[j];
                cnt[j] = count;
                count += temp;
            }
            String[] tempArr = new String[n];
            for (int j = 0; j < n; j++) {
                tempArr[cnt[lines[j].charAt(lines[j].length() - i)]] = lines[j];
                cnt[lines[j].charAt(lines[j].length() - i)]++;
            }
            lines = tempArr;
        }
        return lines;
    }
}