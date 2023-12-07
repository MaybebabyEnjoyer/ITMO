import java.util.*;

public class F {
    private static boolean checkCorrectness(List<Integer> stackB) {
        for (int i = 0; i < stackB.size() - 2; i++) {
            if (stackB.get(i) > stackB.get(i + 1)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] arr = new int[n];
        List<Integer> stackA = new ArrayList<>();
        List<Integer> stackB = new ArrayList<>();
        List<String> answer = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            arr[i] = sc.nextInt();
        }
        for (int i = 0; i < n; i++) {
            while (!stackA.isEmpty() && arr[i] > stackA.get(stackA.size() - 1)) {
                stackB.add(stackA.get(stackA.size() - 1));
                stackA.remove(stackA.size() - 1);
                answer.add("pop");
            }
            stackA.add(arr[i]);
            answer.add("push");
        }
        while (!stackA.isEmpty()) {
            stackB.add(stackA.get(stackA.size() - 1));
            stackA.remove(stackA.size() - 1);
            answer.add("pop");
        }
        if (checkCorrectness(stackB)) {
            for (String operation : answer) {
                System.out.println(operation);
            }
        } else {
            System.out.println("impossible");
        }
    }
}
