import java.io.BufferedReader;
import java.io.*;

public class A {
    public static void main(String[] args) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            String[] nums = in.readLine().split(" ");
            double a = Double.parseDouble(nums[0]);
            double b = Double.parseDouble(nums[1]);
            double n = Double.parseDouble(nums[2]);
            System.out.println(2 * ((int)Math.ceil((n - b) / ( b - a))) + 1);
        } catch (IOException e) {
            System.out.println("Something bad happened... " + e.getMessage());
        }
    }
}