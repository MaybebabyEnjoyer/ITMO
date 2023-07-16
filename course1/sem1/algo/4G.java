import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;

public class Task4G {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Deque<Integer> firstPlayer = new ArrayDeque<>();
        Deque<Integer> secondPlayer = new ArrayDeque<>();
        for (int i = 0; i < 5; i++) {
            firstPlayer.addLast(sc.nextInt());
        }
        for (int i = 0; i < 5; i++) {
            secondPlayer.addLast(sc.nextInt());
        }
        int round = 0;
        while (round < 1000000 && firstPlayer.size() > 0 && secondPlayer.size() > 0) {
            int firstMove = firstPlayer.removeFirst();
            int secondMove = secondPlayer.removeFirst();
            if ((firstMove > secondMove && !(firstMove == 9 && secondMove == 0)) || firstMove == 0 && secondMove == 9) {
                firstPlayer.addLast(firstMove);
                firstPlayer.addLast(secondMove);
            } else {
                secondPlayer.addLast(firstMove);
                secondPlayer.addLast(secondMove);
            }
            round++;
        }
        if (round == 1000000 && firstPlayer.size() > 0 && secondPlayer.size() > 0) {
            System.out.println("botva");
        } else if (firstPlayer.size() == 0) {
            System.out.println("second " + round);
        } else {
            System.out.println("first " + round);
        }
    }
}