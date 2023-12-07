import java.util.*;

public class E {
	private static void heapSort(int n, int[] arr) {
		for (int i = n / 2 - 1; i >= 0; i--) {
			heapify(arr, n, i);
		}
		for (int i = n - 1; i > 0; i--) {
			int temp = arr[0];
			arr[0] = arr[i];
			arr[i] = temp;
			heapify(arr, i, 0);
		}
	}

	private static void heapify(int[] arr, int n, int i) {
		int largest = i;
		int left = 2 * i + 1;
		int right = 2 * i + 2;
		if (left < n && arr[left] > arr[largest]) {
			largest = left;
		}
		if (right < n && arr[right] > arr[largest]) {
			largest = right;
		}
		if (largest != i) {
			int swap = arr[i];
			arr[i] = arr[largest];
			arr[largest] = swap;
			heapify(arr, n, largest);
		}
	}

	private static int lower_bound(int n, int[] arr, int value) {
		int left = 0;
		int right = n;
		while (left < right) {
			int mid =  left + (right - left) / 2;
			if (arr[mid] >= value) {
				right = mid;
			} else {
				left = mid + 1;
			}
		}
		return left;
	}

	private static int upper_bound(int n, int[] arr, int value) {
		int left = 0;
		int right = n;
		while (left < right) {
			int mid =  left + (right - left) / 2;
			if (value >= arr[mid]) {
				left = mid + 1;
			} else {
				right = mid;
			}
		}
		return left;
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		int[] arr = new int[n];
		for (int i = 0; i < n; i++) {
			arr[i] = sc.nextInt();
		}
		heapSort(n, arr);
		int k = sc.nextInt();
		for (int i = 0; i < k; i++) {
			int l = sc.nextInt();
			int r = sc.nextInt();
			int left = lower_bound(n, arr, l);
			int right = upper_bound(n, arr, r);
			System.out.println(right - left);
		}
	}	
}
