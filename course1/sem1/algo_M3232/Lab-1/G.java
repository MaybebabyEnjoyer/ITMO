import java.util.*;

public class G {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		int x = sc.nextInt();
		int y = sc.nextInt();
		int left = 0;
		int right = (n - 1) * Math.max(x, y);
		while (right > left + 1) {
			int mid = (right + left) / 2;
			if ((mid / x + mid / y) < n - 1) {
				left = mid;
			} else {
				right = mid;
			}
		}
		System.out.println(right + Math.min(x, y));
	}
}
