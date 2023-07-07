import java.util.Arrays;

public class IntList {
    private int s = 0;
    private int[] mas = new int[1];

    public void append(int a) {
        if (s == mas.length) {
            mas = Arrays.copyOf(mas, mas.length * 2);
        }
        mas[s++] = a;
    }

    public int get(int num) {
        return mas[num];
    }

    public int size() {
        return s;
    }
}
