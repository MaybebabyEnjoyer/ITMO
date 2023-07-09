package game;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int playerTwoWins = 0;
        int playerOneWins = 0;
        Cell firstTurn = Cell.X;
        while (true) {
            try {
                System.out.println("Input number of wins: ");
                int numOfWins = sc.nextInt();
                System.out.println("Input mnk");
                int m = sc.nextInt();
                int n = sc.nextInt();
                int k = sc.nextInt();
                if (k > m || k > n) {
                    throw new NoSuchElementException();
                }
                final Game game = new Game(false, new HumanPlayer(), new HumanPlayer());
                int result;
                do {
                    result = game.play(new MnkBoard(m, n, k, numOfWins, firstTurn));
                    System.out.println("Game result: " + result);
                    if (result == 1) {
                        playerOneWins++;
                    } else if (result == 2) {
                        playerTwoWins++;
                    }
                    if (playerTwoWins >= numOfWins) {
                        System.out.println("Player2 wins");
                        System.exit(0);
                    } else if (playerOneWins >= numOfWins) {
                        System.out.println("Player1 wins");
                        System.exit(0);
                    }
                    firstTurn = firstTurn == Cell.X ? Cell.O : Cell.X;
                } while (result != 0);
                break;

            } catch (InputMismatchException e) {
                System.out.println("Wrong input, please, try again: ");
                String bad = sc.next();
            } catch (NoSuchElementException e) {
                System.out.println("K can't be more than M or N, please, try again: ");
            } catch (AssertionError e) {
                System.out.println("No such player, try again");
            } catch (NullPointerException e) {
                System.out.println("Something went wrong, please, try again");
            }
        }
    }
}
