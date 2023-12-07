import java.util.*;

public class G {
	private static int find(int x, int[] parents) {
		if (parents[x - 1] == x) {
			return x;
		}
		parents[x - 1] = find(parents[x - 1], parents);
		return parents[x - 1];
	}

	private static void union(int x, int y, int[] parents, int[] length, int[] min, int[] max) {
		x = find(x, parents);
		y = find(y, parents);
		parents[x - 1] = y;
		if (x != y) {
			length[y - 1] += length[x - 1];
			min[y - 1] = Math.min(min[x - 1], min[y - 1]);
			max[y - 1] = Math.max(max[x - 1], max[y - 1]);
		}
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		int[] parents = new int[n];
		int[] length = new int[n];
		int[] min = new int[n];
		int[] max = new int[n];
		for (int i = 0; i < n; i++) {
			parents[i] = i + 1;
			min[i] = i + 1;
			max[i] = i + 1; 
		}
		Arrays.fill(length, 1);
		while (sc.hasNext()) {
			String operation = sc.next();
			if (operation.equals("get")) {
				int x = sc.nextInt();
				int parent = find(x, parents);
				System.out.println(min[parent - 1] + " " + max[parent - 1] + " " + length[parent - 1]);
			} else {
				int x = sc.nextInt();
				int y = sc.nextInt();
				union(x, y, parents, length, min, max);
			}
		}
	}
}
