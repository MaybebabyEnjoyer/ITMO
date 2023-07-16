import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Task4E {

    public static String randString(int len) {

        int left = 97;
        int right = 122;
        Random random = new Random();
        StringBuilder str = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            int randomInt = left + (int) (random.nextFloat() * (right - left + 1));
            str.append((char) randomInt);
        }
        return str.toString();
    }

    public static long getHash(int p, int q, String s) {
        long hash = s.charAt(0);
        for (int i = 1; i < s.length(); i++) {
            hash = (hash * p + s.charAt(i)) % q;
        }
        return hash;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int p = sc.nextInt();
        int q = sc.nextInt();
        Map<Long, String> m = new HashMap<>();
        Random random = new Random();
        while (true) {
            String s = randString(random.nextInt(1, 100));
            long hash = getHash(p, q, s);
            if (m.containsKey(hash) && !m.get(hash).equals(s)) {
                System.out.println(s + " " + m.get(hash));
                break;
            }
            m.put(hash, s);
        }
    }
}