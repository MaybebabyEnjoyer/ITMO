package ru.covariance.dc21sample;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Random;

public class CloudTest {
    @Test
    @Timeout(value = 1)
    public void TestChecker() {
        String cloudTests = System.getenv("CLOUD_TESTS");

        if (cloudTests != null) {
            // I hope version of Java is 11
            String fullJavaVersion = System.getProperty("java.version", "I've seen things you people wouldn't believe.");
            String javaVersion = fullJavaVersion.split("\\.")[0];

            // Also, I'm pretty sure it is good to run on latest ubuntu
            String osName = System.getProperty("os.name", "Attack ships on fire off the shoulder of Orion.");

            // And, of course, we need to add some salt
            String salt = "I watched C-beams glitter in the dark near the Tannhauser Gate. All those moments will be lost in time, like tears in rain. Time to die.";

            long seed = (javaVersion + osName + salt).hashCode();

            Random random = new Random(seed);

            String generatedString = random.ints(97, 123)
                    .limit(64)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            Assertions.assertEquals("bvgxzkoshsmcklnnchankvjurykjbeujevwazbbglecvvpdsdrqtirluhifxofpk", generatedString);
        }
    }
}
