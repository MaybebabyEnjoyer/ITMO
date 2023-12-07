import java.util.*;

public class H {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		double c = Double.parseDouble(sc.next());
		double left = 1.0; 
		double right = 10000000000.0;
		while (Math.abs(left - right) > 1e-10) {
			double mid = (left + right) / 2;
			if ((Math.pow(mid, 2) + Math.sqrt(mid)) > c) {
				right = mid;
			} else {
				left = mid;
			}
		}
		System.out.println(right);
	}
}
