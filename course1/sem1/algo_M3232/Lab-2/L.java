import java.util.*;

public class L {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		Long[] sequence = new Long[n];
		for (int i = 0; i < n; i++) {
			sequence[i] = sc.nextLong();
		}
		int[] dynamics = new int[n];
		int[] prev = new int[n];
		for (int i = 0; i < n; i++) {
			dynamics[i] = 1;
			prev[i] = -1;
			for (int j = 0; j < i; j++) {
				if (sequence[i] > sequence[j] && 1 + dynamics[j] > dynamics[i]) {
					dynamics[i] = dynamics[j] + 1;
					prev[i] = j;
				}
			}
		}
		int pos = 0;
		int k = dynamics[0];
		for (int i = 0; i < n; i++) {
			if (dynamics[i] > k) {
				k = dynamics[i];
				pos = i;
			}
		}
		System.out.println(k);
		Long[] answer = new Long[k];
		int index = 0;
		while (pos != -1) {
			answer[index] = sequence[pos];
			index++;
			pos = prev[pos];
		}
		for (int i = k - 1; i >= 0; i--) {
			System.out.print(answer[i] + " ");
		}
	}
}