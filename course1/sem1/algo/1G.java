import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Task1G {

    private static int partition(int[] a, int l, int r) {
        int m = a[(l + r) / 2];
        int i = l;
        int j = r;
        while (i <= j) {
            while (a[i] > m) {
                i++;
            }
            while (a[j] < m) {
                j--;
            }
            if (i >= j) {
                break;
            }
            int temp = a[i];
            a[i] = a[j];
            a[j] = temp;
            i++;
            j--;
        }
        return j;
    }

    private static void quicksort(int[] a, int l, int r) {
        if (l < r) {
            int q = partition(a, l, r);
            quicksort(a, l, q);
            quicksort(a, q + 1, r);
        }
    }

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String[] temp = reader.readLine().split(" ");
            int n = Integer.parseInt(temp[0]);
            int m = Integer.parseInt(temp[1]);
            int p = Integer.parseInt(temp[2]);
            temp = reader.readLine().split(" ");
            int[] a = new int[n];

            int bucketCounter = m;
            long honeyCount = 0;

            for (int i = 0; i < a.length; i++) {
                a[i] = Integer.parseInt(temp[i]);
                while (a[i] >= p && bucketCounter > 0) {
                    honeyCount += p;
                    bucketCounter--;
                    a[i] -= p;
                }
            }

            if (bucketCounter == 0) {
                System.out.println(honeyCount);
            } else {
                quicksort(a, 0, n - 1);
                int i = 0;
                while (bucketCounter > 0 && i < n) {
                    bucketCounter--;
                    honeyCount += a[i];
                    i++;
                }
                System.out.println(honeyCount);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}