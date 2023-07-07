import java.io.IOException;
import java.util.Arrays;
public class ReverseTranspose {
    public static void main(String[] args) throws IOException {
        int i = 0;
        int k = 0;
        Scanner sc = new Scanner();
        int[][] numbers = new int[1][];
        int[] temp = new int[1];
        int maxLen = 0;
        while (sc.hasInput()) {
            int j = 0;
            while (sc.hasNextChar()) {
                if (j > maxLen) {
                    maxLen = j;
                }
                if (temp.length == j) {
                    temp = Arrays.copyOf(temp, temp.length * 2);
                }
                temp[j++] = sc.nextInt();
            }
            if (i == numbers.length) {
                numbers = Arrays.copyOf(numbers, numbers.length * 2);
            }
            numbers[i++] = Arrays.copyOf(temp, j);
            k++;
        }
        maxLen++;
        numbers = Arrays.copyOf(numbers, k);
        StringBuilder sb = new StringBuilder();
        for (int num1 = 0; num1 < maxLen; num1++) {
            for (int[] number : numbers) {
                if (num1 < number.length) {
                    sb.append(number[num1]).append(" ");
                }
            }
            if (num1 < maxLen - 1) {
                sb.append(System.lineSeparator());
            }
        }
        System.out.println(sb);
    }
}