package ru.covariance.dc21sample;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class TestMath {
    @Test
    @Timeout(value = 1)
    public void TestAtan() {
        Assertions.assertEquals(1.4711276743037345918528755717617308518553063771832382624719635193, Math.Atan(10));
    }

    @Test
    @Timeout(value = 1)
    public void TestTan() {
        Assertions.assertEquals(1.5574077246549022305069748074583601730872507723815200383839466056, Math.Tan(1));
    }
}
