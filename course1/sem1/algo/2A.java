import java.util.Scanner;

public class Task2A {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int m = sc.nextInt();
        int k = sc.nextInt();
        boolean flag = false;
        sc.nextLine();
        String[][] layers = new String[k][];
        for (int i = 0; i < k; i++) {
            layers[i] = sc.nextLine().split(" ");
        }
        for (int i = 0; i < (int) Math.pow(2, n); i++) {
            String bin = Integer.toBinaryString(i);
            StringBuilder vector = new StringBuilder("0".repeat(n - bin.length()) + Integer.toBinaryString(i));
            int l = 0;
            while (l < k) {
                int compCount = Integer.parseInt(layers[l][0]);
                int counter = 1;
                while (compCount > 0) {
                    int firstInd = Integer.parseInt(layers[l][counter++]) - 1;
                    int secondInd = Integer.parseInt(layers[l][counter++]) - 1;
                    if (Character.getNumericValue(vector.charAt(firstInd)) > Character.getNumericValue(vector.charAt(secondInd))) {
                        int maxInd = Math.max(firstInd, secondInd);
                        char temp = vector.charAt(secondInd);
                        vector.setCharAt(maxInd, vector.charAt(firstInd));
                        vector.setCharAt(firstInd + secondInd - maxInd, temp);
                    } else {
                        int maxInd = Math.max(firstInd, secondInd);
                        char temp = vector.charAt(firstInd);
                        vector.setCharAt(maxInd, vector.charAt(secondInd));
                        vector.setCharAt(firstInd + secondInd - maxInd, temp);
                    }
                    compCount--;
                }
                l++;
            }
            for (int j = 0; j < n - 1; j++) {
                if (Character.getNumericValue(vector.charAt(j)) > Character.getNumericValue(vector.charAt(j + 1))) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                System.out.println("No");
                break;
            }
        }
        if (!flag) {
            System.out.println("Yes");
        }
    }
}