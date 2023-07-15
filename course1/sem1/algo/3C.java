import java.util.Locale;
import java.util.Scanner;

public class Task3C {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in).useLocale(Locale.US);
        int N = sc.nextInt();
        double A = sc.nextDouble();
        double[] h = new double[N];
        h[0] = A;
        double l = 0;
        double r = 1000;
        boolean flag;
        while (r - l > 0.00000001) {
            flag = false;
            double m = (r + l) / 2;
            h[1] = m;
            for (int i = 1; i < N - 1; i++) {
                h[i + 1] = 2 * (h[i] + 1) - h[i - 1];
                if (h[i + 1] <= 0) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                l = m;
            } else {
                r = m;
            }
        }
        System.out.printf(Locale.US, "%.2f", h[N - 1]);
    }
}