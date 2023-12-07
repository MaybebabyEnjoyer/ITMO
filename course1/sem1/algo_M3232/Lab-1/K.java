import java.util.*;

public class K {
	private static long minMaxSubarraySum(long[] arr, int n, int k, long left, long right) {
		long result = 0;
		while (left <= right) {
			long mid = (left + right) / 2;
			if (func(arr, n, k, mid)) {
				result = mid;
				right = mid - 1;
			} else {
				left = mid + 1;
			}
		}
		return result;
	}

	private static boolean func(long[] arr, int n, int k, long mid) {
		long sum = 0;
		int c = 0;
		for (int i = 0; i < n; i++) {
			if (mid < arr[i]) {
				return false;
			}
			sum += arr[i];
			if (mid < sum) {
				sum = arr[i];
				c++;
			}
		}
		c++;
        return c <= k;
    }

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		int k = sc.nextInt();
		long[] arr = new long[n];
		for (int i = 0; i < n; i++) {
			arr[i] = sc.nextLong();
		}
		long left = 0;
		long right = 0;
		for (int i = 0; i < n; i++) {
			if (left < arr[i]) {
				left = arr[i];
			}
		}
		for (int i = 0; i < n; i++) {
			right += arr[i];
		}
		System.out.println(minMaxSubarraySum(arr, n, k, left, right));
    }
}
