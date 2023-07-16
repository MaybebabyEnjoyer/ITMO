import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Task3G {
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            int n = Integer.parseInt(reader.readLine());

            for (int i = 0; i < n; i++) {
                String[] temp = reader.readLine().split(" ");
                long imp = Long.parseLong(temp[0]);
                long crew = Long.parseLong(temp[1]);

                long winner = crew - ((imp + 1) * imp / 2);

                if (winner > 0) {
                    System.out.println("Crewmates");
                    System.out.println(imp);
                } else {
                    long l = 1;
                    long r = imp;
                    while (l < r - 1) {
                        long m = (r + l) / 2;
                        long frm = (((2 * imp) - (m - 1)) * m) / 2;
                        if (frm >= crew) {
                            r = m;
                        } else {
                            l = m;
                        }
                    }
                    System.out.println("Impostors");
                    if ((((2 * imp) - (l - 1)) * l) / 2 >= crew) {
                        System.out.println(l);
                    } else {
                        System.out.println(l + 1);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}