package wordStat;

import base.*;

import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Tests for <a href="https://www.kgeorgiy.info/courses/prog-intro/homeworks.html#wordstat">Word Statistics</a> homework
 * of <a href="https://www.kgeorgiy.info/courses/prog-intro/">Introduction to Programming</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class WordStatTest {
    private static final Named<Comparator<Pair<String, Integer>>> INPUT = Named.of("Input", Comparator.comparingInt(p -> 0));
    private static final Named<Comparator<Pair<String, Integer>>> WORDS = Named.of("Words", Comparator.comparing(Pair::first));

    private static final int SIZE = 3;

    private static final Named<Function<String, Stream<String>>> ID  = Named.of("", Stream::of);
    private static final Named<Function<String, Stream<String>>> PREFIX_ALL =
            Named.of("Prefix", s -> Stream.of(s.substring(0, Math.min(SIZE, s.length()))));
    private static final Named<Function<String, Stream<String>>> SHINGLES_ALL =
            Named.of("Shingles", s -> s.length() > SIZE
                                       ? IntStream.rangeClosed(0, s.length() - SIZE).mapToObj(i -> s.substring(i, i + SIZE))
                                       : Stream.of(s));
    private static final Named<Function<String, Stream<String>>> SUFFIX_ALL =
            Named.of("Suffix", s -> Stream.of(s.substring(Math.max(0, s.length() - SIZE))));


    public static final Selector SELECTOR = new Selector(WordStatTester.class)
            .variant("Base",            WordStatTester.variant(INPUT, ID))
            .variant("WordsPrefix",     WordStatTester.variant(WORDS, PREFIX_ALL))
            .variant("WordsShingles",   WordStatTester.variant(WORDS, SHINGLES_ALL))
            .variant("Words",           WordStatTester.variant(WORDS, ID))
            .variant("WordsSuffix",     WordStatTester.variant(WORDS, SUFFIX_ALL))

            ;

    private WordStatTest() {
        // Utility class
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}
