import java.util.*;

public class B {
    private static void countingSort(int n, int[] arr) {
        int[] out = new int[n];
        int[] k = new int[101];
        for (int i = 0; i < 101; i++) {
            k[i] = 0;
        }
        for (int i = 0; i < n; i++) {
            k[arr[i]]++;
        }
        for (int i = 1; i <= 100; i++) {
            k[i] += k[i - 1];
        }
        for (int i = n - 1; i >= 0; i--) {
            out[k[arr[i]] - 1] = arr[i];
            k[arr[i]]--;
        }
        for (int i = 0; i < n; i++) {
            arr[i] = out[i];
        }
    }

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		int[] arr = new int[n];
		for (int i = 0; i < n; i++) {
			arr[i] = sc.nextInt();
		}
		countingSort(n, arr);
		for (int i = 0; i < n; i++) {
			System.out.print(arr[i] + " ");
		}
	}
}
