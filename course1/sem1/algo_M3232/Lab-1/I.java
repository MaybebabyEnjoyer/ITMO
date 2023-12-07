import java.util.*;

public class I {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		double v_p = Double.parseDouble(sc.next());
		double v_f = Double.parseDouble(sc.next());
		double a_y = Double.parseDouble(sc.next());
		double left = 0.0;
		double right = 1.0; 
		while (right - left > 1e-8) {
			double middle_left = left + (right - left) / 3;
			double middle_right = right - (right - left) / 3;
			if ((Math.sqrt(Math.pow(middle_left, 2) + Math.pow((1 - a_y), 2)) / v_p) + (Math.sqrt(Math.pow(a_y, 2) + Math.pow((1 - middle_left), 2)) / v_f) >
					(Math.sqrt(Math.pow(middle_right, 2) + Math.pow((1 - a_y), 2)) / v_p) + (Math.sqrt(Math.pow(a_y, 2) + Math.pow((1 - middle_right), 2)) / v_f)) {
				left = middle_left;
			} else {
				right = middle_right;
			}
		}
		System.out.println(right);
	}
}
