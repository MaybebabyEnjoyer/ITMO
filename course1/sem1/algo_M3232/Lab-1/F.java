import java.util.*;

public class F {
	private static int lower_bound_abs(List<Integer> arr, int value) {
		int left = 0;
		int right = arr.size() - 1;
		while (left < right) {
			int mid =  left + (right - left) / 2;
			if (arr.get(mid) >= value) {
				right = mid;
			} else {
				left = mid + 1;
			}
		}
		if (left == 0) {
			return arr.get(left);
		} else {
			if ((arr.get(left) - value) >= (value - arr.get(left - 1))) {
				return arr.get(left - 1);
			} else {
				return arr.get(left);
			}
		}
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		int k = sc.nextInt();
		List<Integer> arr = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			arr.add(sc.nextInt());
		}
		for (int i = 0; i < k; i++) {
			System.out.println(lower_bound_abs(arr, sc.nextInt()));
		}
	}
}
