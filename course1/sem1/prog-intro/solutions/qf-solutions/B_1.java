import java.io.BufferedReader;
import java.io.*;

public class B {
    public static void main(String[] args) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            int n = Integer.parseInt(in.readLine());
            for (int i = -25000 * 710; n > 0; i += 710) {
                System.out.println(i);
                n--;
            }
        } catch (IOException e) {
            System.out.println("Something bad happened... " + e.getMessage());
        }
    }
}