public class Pair {
    private final int K;
    private final int V;

    public Pair(int K, int V) {
        this.K = K;
        this.V = V;
    }

    public String getPair() {
        return K + ":" + V;
    }
}
