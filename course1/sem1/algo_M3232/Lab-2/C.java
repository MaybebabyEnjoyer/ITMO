import java.util.*;

public class C {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int tail = 0;
        int head = 0;
        int[] queue = new int[100001];
        int[] index = new int[100001];
        for (int i = 0; i < n; i++) {
            int operation = sc.nextInt();
            switch (operation) {
                case 1 -> {
                    int id = sc.nextInt();
                    queue[tail] = id;
                    index[id] = tail;
                    tail++;
                }
                case 2 -> head++;
                case 3 -> tail--;
                case 4 -> {
                    int q = sc.nextInt();
                    System.out.println(index[q] - head);
                }
                case 5 -> System.out.println(queue[head]);
            }
        }
    }
}
