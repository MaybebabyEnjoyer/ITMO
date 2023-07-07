import java.io.IOException;
import java.util.Arrays;

public class Reverse {
    public static void main(String[] args) {
        try {
            int i = 0;
            int k = 0;
            Scanner sc = new Scanner();
            int[][] numbers = new int[1][];
            int[] temp = new int[1];
            while (sc.hasInput()) {
                int j = 0;
                while (sc.hasNextChar()) {
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
            numbers = Arrays.copyOf(numbers, k);
            StringBuilder sb = new StringBuilder();
            for (i = i - 1; i >= 0; i--) {
                for (int j = numbers[i].length - 1; j >= 0; j--) {
                    sb.append(numbers[i][j]).append(" ");
                }
                if (i > 0) {
                    sb.append(System.lineSeparator());
                }
            }
            System.out.println(sb);
        } catch (IOException e) {
            System.out.println("I/O Exception: " + e.getMessage());
        }
    }
}