import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Run this code with provided arguments.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@SuppressWarnings("MagicNumber")
public final class RunMe {
    private RunMe() {
        // Utility class
    }

    public static void main(final String[] args) {
        final byte[] password = parseArgs(args);

        key0(password);
        System.out.println("The first key was low-hanging fruit, can you find others?");
        System.out.println("Try to read, understand and modify code in keyX(...) functions");

        key1(password);
        key2(password);
        key3(password);
        key4(password);
        key5(password);
        key6(password);
        key7(password);
        key8(password);
        key9(password);
        key10(password);
        key12(password);
        key13(password);
        key14(password);
        key15(password);
        key16(password);
//        key17(password);
    }

    private static void key0(final byte[] password) {
        // The result of print(...) function depends only on explicit arguments
        print(0, 0, password);
    }


    private static void key1(final byte[] password) {
        while ("1".length() == 1) {
        }

        print(1, 5487520208732485324L, password);
    }


    private static void key2(final byte[] password) {
        int result = 0;
        for (int i = 0; i < 300_000; i++) {
            for (int j = 0; j < 300_000; j++) {
                for (int k = 0; k < 300_000; k++) {
                    result ^= (i * 7) | (j + k);
                    result ^= result << 1;
                }
            }
        }

        print(2, 2475802485702485454L, password);
    }


    private static void key3(final byte[] password) {
        int result = 0;
        for (int i = 0; i < 2022; i++) {
            for (int j = 0; j < 2022; j++) {
                for (int k = 0; k < 2022; k++) {
                    for (int p = 0; p < 12; p++) {
                        result ^= (i * 7) | (j + k * 5) & ~p;
                        result ^= result << 1;
                    }
                }
            }
        }

        print(3, result, password);
    }


    private static void key4(final byte[] password) {
        for (long i = Long.MIN_VALUE; i < Long.MAX_VALUE; i++) {
            if ((i ^ (i >>> 32)) == 8329482348347087432L) {
                print(4, i, password);
            }
        }
    }


    private static final long PRIME = 1073741783;

    private static void key5(final byte[] password) {
        final long n = 1_000_000_000_000_000L + ByteBuffer.wrap(password).getInt();

        long result = 0;
        for (long i = 0; i < n; i++) {
            result = (result + i / 2 + i / 3 + i / 4 + i / 2022) % PRIME;
        }

        print(5, result, password);
    }


    private static void key6(final byte[] password) {
        /***
            \u002a\u002f\u0077\u0068\u0069\u006c\u0065\u0020\u0028\u0022\u0031\u0022
            \u002e\u006c\u0065\u006e\u0067\u0074\u0068\u0028\u0029\u0020\u003d\u003d
            \u0020\u0031\u0029\u003b\u0020\u0020\u006c\u006f\u006e\u0067\u0020\u0009
            \u0020\u0020\u0072\u0065\u0073\u0075\u006c\u0074\u0020\u003d\u0020\u000a
            \u0034\u0030\u0033\u0039\u0032\u0038\u0034\u0030\u0039\u0033\u0038\u004c
            \u002b\u0070\u0061\u0073\u0073\u0077\u006f\u0072\u0064\u005b\u0033\u005d
            \u003b\u002f\u002a
        ***/
        print(6, result, password);
    }


    private static void key7(final byte[] password) {
        // Count the number of occurrences of the most frequent noun at the following page:
        // https://docs.oracle.com/javase/specs/jls/se17/html/jls-6.html

        // The singular form of the most frequent noun
        final String singular = "";
        // The plural form of the most frequent noun
        final String plural = "";
        // The total number of occurrences
        final int total = 0;
        if (total != 0) {
            print(7, (singular + ":" + plural + ":" + total).hashCode(), password);
        }
    }


    private static void key8(final byte[] password) {
        // Count the number of red (#ff0000) pixes of this image:
        // https://docs.oracle.com/javase/webdesign/other/im/oralogo_small.gif

        final int number = 0;
        if (number != 0) {
            print(8, number, password);
        }
    }


    private static final String PATTERN = "Reading the documentation can be surprisingly helpful!";
    private static final int SMALL_REPEAT_COUNT = 10_000_000;

    private static void key9(final byte[] password) {
        String repeated = "";
        for (int i = 0; i < SMALL_REPEAT_COUNT; i++) {
            repeated += PATTERN;
        }

        print(9, repeated.hashCode(), password);
    }


    private static final long LARGE_REPEAT_SHIFT = 27;
    private static final long LARGE_REPEAT_COUNT = 1L << LARGE_REPEAT_SHIFT;

    private static void key10(final byte[] password) {
        String repeated = "";
        for (long i = 0; i < LARGE_REPEAT_COUNT; i++) {
            repeated += PATTERN;
        }

        print(10, repeated.hashCode(), password);
    }


    private static void key11(final byte[] password) {
        print(11, 8434985024938509435L, password);
    }


    private static void key12(final byte[] password) {
        final BigInteger year = BigInteger.valueOf(-2022);
        final BigInteger prime = BigInteger.valueOf(PRIME);

        final long result = Stream.iterate(BigInteger.ZERO, BigInteger.ONE::add)
                .filter(i -> year.multiply(i).add(prime).multiply(i).compareTo(BigInteger.ZERO) > 0)
                .mapToLong(i -> i.longValue() * password[i.intValue() % password.length])
                .sum();

        print(12, result, password);
    }


    private static final long MAX_DEPTH = 100_000_000L;

    private static void key13(final byte[] password) {
        try {
            key13(password, 0, 0);
        } catch (final StackOverflowError e) {
            System.err.println("Stack overflow :((");
        }
    }

    private static void key13(final byte[] password, final long depth, final long result) {
        if (depth < MAX_DEPTH) {
            key13(password, depth + 1, (result ^ 234782022) + (result << 2) + depth * 17);
        } else {
            print(13, result, password);
        }
    }


    private static void key14(final byte[] password) {
        final Instant today = Instant.parse("2022-09-06T00:00:00Z");
        final BigInteger hours = BigInteger.valueOf(Duration.between(Instant.EPOCH, today).toHours());

        final long result = Stream.iterate(BigInteger.ZERO, BigInteger.ONE::add)
                .map(hours::multiply)
                .reduce(BigInteger.ZERO, BigInteger::add)
                .longValue();

        print(14, result, password);
    }

    private static void key15(final byte[] password) {
        // REDACTED
    }

    private static void key16(final byte[] password) {
        byte a1 = (byte) (password[0] + password[1]);
        byte a2 = (byte) (password[2] + password[3]);
        byte a3 = (byte) (password[4] + password[5]);

        for (long i = 1_000_000_000_000_000L + ByteBuffer.wrap(password).getInt(); i >= 0; i--) {
            a1 ^= a2;
            a2 += a3 & a1;
            a3 -= a1;
        }

        key16(password, a1, a2, a3);
    }

    private static void key16(final byte[] password, final byte a1, final byte a2, final byte a3) {
        final String result = a1 + " " + a2 + " " + a3;
        print(16, result.hashCode(), password);
    }

    private static void key17(final byte[] password) {
        print(17, calc17(Math.abs(Arrays.toString(password).hashCode() % 2022)), password);
    }

    /**
     * Write me
     * <pre>
     *    0: iconst_0
     *    1: istore_1
     *    2: iload_1
     *    3: bipush        42
     *    5: idiv
     *    6: iload_0
     *    7: isub
     *    8: ifge          17
     *   11: iinc          1, 1
     *   14: goto          2
     *   17: iload_1
     *   18: ireturn
     * </pre>
     */
    private static int calc17(final int n) {
        return n;
    }

    // ---------------------------------------------------------------------------------------------------------------
    // You may ignore all code below this line.
    // It is not required to get any of the keys.
    // ---------------------------------------------------------------------------------------------------------------

    private static void print(final int no, long result, final byte[] password) {
        final byte[] key = password.clone();
        for (int i = 0; i < 6; i++) {
            key[i] ^= result;
            result >>>= 8;
        }

        System.out.format("Key %d: https://www.kgeorgiy.info/courses/prog-intro/hw1/%s%n", no, key(SALT, key));
    }

    private static String key(final byte[] salt, final byte[] data) {
        DIGEST.update(salt);
        DIGEST.update(data);
        DIGEST.update(salt);
        final byte[] digest = DIGEST.digest();

        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            if (i != 0) {
                sb.append("-");
            }
            sb.append(KEYWORDS.get(digest[i] & 63));
        }
        return sb.toString();
    }

    private static byte[] parseArgs(final String[] args) {
        if (args.length != 6) {
            throw error("Expected 6 command line arguments, found: %d", args.length);
        }

        final byte[] bytes = new byte[args.length];
        for (int i = 0; i < args.length; i++) {
            final Byte value = VALUES.get(args[i].toLowerCase(Locale.US));
            if (value == null) {
                throw error("Expected keyword, found: %s", args[i]);
            }
            bytes[i] = value;
        }
        return bytes;
    }

    private static AssertionError error(final String format, final Object... args) {
        System.err.format(format, args);
        System.err.println();
        System.exit(1);
        throw new AssertionError();
    }

    private static final MessageDigest DIGEST;
    static {
        try {
            DIGEST = MessageDigest.getInstance("SHA-256");
        } catch (final NoSuchAlgorithmException e) {
            throw new AssertionError("Cannot create SHA-256 digest", e);
        }
    }

    private static final byte[] SALT = "jig6`wriusoonBaspaf9TuRutabyikUch/Bleir3".getBytes(StandardCharsets.UTF_8);

    private static final List<String> KEYWORDS = List.of(
            "abstract",
            "assert",
            "boolean",
            "break",
            "byte",
            "case",
            "catch",
            "char",
            "class",
            "const",
            "continue",
            "default",
            "do",
            "double",
            "else",
            "enum",
            "extends",
            "false",
            "final",
            "finally",
            "float",
            "for",
            "goto",
            "if",
            "implements",
            "import",
            "instanceof",
            "int",
            "interface",
            "long",
            "native",
            "new",
            "null",
            "package",
            "private",
            "protected",
            "public",
            "return",
            "short",
            "static",
            "strictfp",
            "super",
            "switch",
            "synchronized",
            "this",
            "throw",
            "throws",
            "transient",
            "true",
            "try",
            "var",
            "void",
            "volatile",
            "while",
            "Exception",
            "Error",
            "Object",
            "Number",
            "Integer",
            "Character",
            "String",
            "Math",
            "Runtime",
            "Throwable"
    );

    private static final Map<String, Byte> VALUES = IntStream.range(0, KEYWORDS.size())
            .boxed()
            .collect(Collectors.toMap(index -> KEYWORDS.get(index).toLowerCase(Locale.US), Integer::byteValue));
}
