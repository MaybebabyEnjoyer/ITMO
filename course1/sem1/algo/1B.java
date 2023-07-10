import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Inversions {
    static int partition(int[] arr, int l, int r) {
        int pivot = arr[r];
        int i = (l - 1);
        for (int j = l; j <= r - 1; j++) {
            if (arr[j] < pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        int temp = arr[i + 1];
        arr[i + 1] = arr[r];
        arr[r] = temp;
        return (i + 1);
    }
    static int nth_element(int[] arr, int l, int r, int n) {
        if (l == r) {
            return arr[l];
        }
        int p = partition(arr, l, r);
        if (p == n) {
            return arr[p];
        } else if (p > n) {
            return nth_element(arr, l, p - 1, n);
        } else {
            return nth_element(arr, p + 1, r, n);
        }
    }
    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String[] nums = in.readLine().split(" ");
        int n = Integer.parseInt(nums[0]);
        int a0 = Integer.parseInt(nums[1]);
        int k = Integer.parseInt(nums[2]);
        int[] abc = new int[n];
        int j = 1;
        abc[0] = a0;
        for (int i = 0; i < n - 1; i++) {
            abc[j] = (int) ((1103515245 * (long) (abc[j - 1]) + 12345) % 2147483648L);
            j++;
        }
        System.out.println(nth_element(abc, 0, n - 1, k));
    }
}