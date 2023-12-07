import java.util.*;

public class C {
	private static long mergeCount(long[] arr, int left, int right) {
		long inversions = 0;
		if (left < right) {
			int mid = (left + right) / 2;
			inversions += mergeCount(arr, left, mid);
			inversions += mergeCount(arr, mid + 1, right);
			inversions += mergeArrayCount(arr, left, mid, right);
		}
		return inversions;
	}

	private static long mergeArrayCount(long[] arr, int left, int mid, int right) {
		int i = 0;
		int j = 0;
		int k = left;
		long inversions = 0;
		long[] Left = Arrays.copyOfRange(arr, left, mid + 1);
		long[] Right = Arrays.copyOfRange(arr, mid + 1, right + 1);
		while (i < Left.length && j < Right.length) {
			if (Left[i] <= Right[j])
				arr[k++] = Left[i++];
			else {
				arr[k++] = Right[j++];
				inversions += (mid + 1) - (left + i);
			}
		}
		while (i < Left.length) {
			arr[k++] = Left[i++];
		}
		while (j < Right.length) {
			arr[k++] = Right[j++];
		}
		return inversions;
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		long[] arr = new long[n];
		for (int i = 0; i < n; i++) {
			arr[i] = sc.nextLong();
		}
		System.out.println(mergeCount(arr, 0, n - 1));
	}
}
