package wordStat;

import base.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class WordStatChecker extends BaseChecker {
    public static final String DASH = "-֊־‒–—―⸗⸚⸺〰゠︱︲﹘﹣－'";
    public static final String SIMPLE_DELIMITERS = " \t";
    public static final String ADVANCED_DELIMITERS = " \t!\"#$%&()*+,./0123456789:;<=>?@[\\]^_`{|}~ ¡¢£¤¥¦§¨©«¬\u00AD®¯°±²³´¶·¸¹»¼½¾¿×÷˂˃˄˅˒˓˔˕˖˗˘˙˚˛˜˝₴₵₶₷₸₹₺\u20BB\u20BC\u20BD\u20BE";
    public static final String ALL = ExtendedRandom.RUSSIAN + ExtendedRandom.ENGLISH + ExtendedRandom.GREEK + DASH;
    private static final Pattern PATTERN = Pattern.compile("[ ,.:\\d\\[\\]()]+");
    public static final Runner.Packages RUNNER = Runner.packages("", "wordstat", "wspp");

    private final Function<String[][], ? extends List<? extends Pair<?, ?>>> processor;

    protected final MainChecker main;

    private WordStatChecker(
            final String className,
            final Function<String[][], ? extends List<? extends Pair<?, ?>>> processor,
            final TestCounter counter
    ) {
        super(counter);
        main = new MainChecker(RUNNER.files(className));
        this.processor = processor;
    }

    public static void test(
            final TestCounter counter,
            final String className,
            final Function<String[][], ? extends List<? extends Pair<?, ?>>> processor,
            final Consumer<WordStatChecker> tests
    ) {
        tests.accept(new WordStatChecker(className, processor, counter));
    }

    @SuppressWarnings("OverloadedVarargsMethod")
    public void test(final String... lines) {
        test(lines, processor.apply(Arrays.stream(lines).map(PATTERN::split).toArray(String[][]::new)));
    }

    protected void randomTest(final int wordLength, final int totalWords, final int wordsPerLine, final int lines, final String chars, final String delimiters, final Function<String[][], List<? extends Pair<?, ?>>> processor) {
        final String[] words = generateWords(wordLength, totalWords, chars);
        final String[][] text = generateTest(lines, words, wordsPerLine);
        test(input(text, delimiters), processor.apply(text));
    }

    public void randomTest(final int wordLength, final int totalWords, final int wordsPerLine, final int lines, final String chars, final String delimiters) {
        randomTest(wordLength, totalWords, wordsPerLine, lines, chars, delimiters, processor::apply);
    }

    protected void test(final String[] text, final List<? extends Pair<?, ?>> expected) {
        final List<String> expectedList = expected.stream()
                .map(p -> p.first + " " + p.second)
                .collect(Collectors.toList());
        main.testEquals(counter, Arrays.asList(text), expectedList);
    }

    public void test(final String[][] text, final String delimiters, final List<Pair<String, Integer>> answer) {
        test(input(text, delimiters), answer);
    }

    private String[] generateWords(final int wordLength, final int totalWords, final String chars) {
        final String allChars = chars + chars.toUpperCase();
        return IntStream.range(0, totalWords)
                .mapToObj(i -> random().randomString(allChars, wordLength / 2, wordLength))
                .toArray(String[]::new);
    }

    private String[][] generateTest(final int lines, final String[] words, final int wordsPerLine) {
        final String[][] text = new String[lines][];
        for (int i = 0; i < text.length; i++) {
            text[i] = new String[random().nextInt(wordsPerLine / 2, wordsPerLine)];
            for (int j = 0; j < text[i].length; j++) {
                text[i][j] = random().randomItem(words);
            }
        }
        return text;
    }

    private String[] input(final String[][] text, final String delimiters) {
        final String[] input = new String[text.length];
        for (int i = 0; i < text.length; i++) {
            final String[] line = text[i];
            final StringBuilder sb = new StringBuilder(random().randomString(delimiters));
            for (final String word : line) {
                sb.append(word).append(random().randomString(delimiters));
            }
            input[i] = sb.toString();
        }
        return input;
    }
}
