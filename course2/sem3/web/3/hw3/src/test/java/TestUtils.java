public class TestUtils {
    public static void assertNearlyEquals(int expected, int actual) {
        if (!(actual >= expected * 0.8 && actual <= expected * 1.25)) {
            throw new RuntimeException("Failed assertNearlyEquals: expected " + expected + ", actual " + actual + ".");
        }
    }
}
