import java.io.IOException;
import java.util.Arrays;

public class ReverseAbc {
    public static void main(String[] args) {
        try {
            int i = 0;
            int k = 0;
            Scanner sc = new Scanner();
            String[][] numbers = new String[1][];
            String[] temp = new String[1];
            while (sc.hasInput()) {
                int j = 0;
                while (sc.hasNextChar()) {
                    if (temp.length == j) {
                        temp = Arrays.copyOf(temp, temp.length * 2);
                    }
                    temp[j++] = sc.next();
                }
                if (i == numbers.length) {
                    numbers = Arrays.copyOf(numbers, numbers.length * 2);
                }
                numbers[i++] = Arrays.copyOf(temp, j);
                k++;
            }
            numbers = Arrays.copyOf(numbers, k);
            for (i = i - 1; i >= 0; i--) {
                for (int j = numbers[i].length - 1; j >= 0; j--) {
                    System.out.print(numbers[i][j] + " ");
                }
                System.out.println();
            }
        } catch (IOException e) {
            System.out.println("I/O Exception: " + e.getMessage());
        }
    }
}