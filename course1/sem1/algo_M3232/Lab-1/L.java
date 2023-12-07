import java.util.*;

public class L {
	private static long func(long mid, long n) {
		long k = 0; 
		for (int i = 1; i <= n; i++) {
			if (mid/i < n) {
				k += mid / i;
			} else {
				k += n;
			}
		}
		return k;
	}

	private static long binarySearch(long left, long right, long n, long k) {
		while (left < right) {
			long mid = (left + right) / 2;
			if (func(mid, n) < k) {
				left = mid + 1;
			} else {
				right = mid;
			}
		}
		return right;
	} 
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		long n = sc.nextLong();
		long k = sc.nextLong();
		long left = 0;
		long right = n * n;
		System.out.println(binarySearch(left, right, n, k)); 
	}
}
