package md2html;

import base.*;

import java.util.*;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class Md2HtmlTester {
    private final Map<String, Generator> elements = new HashMap<>();
    private final Map<String, List<String>> tags = new LinkedHashMap<>();
    private final List<Pair<String, String>> tests = new ArrayList<>();

    public Md2HtmlTester() {
        addElement("em", "*");
        addElement("em", "_");
        addElement("strong", "**");
        addElement("strong", "__");
        addElement("s", "--");
        addElement("code", "`");

        test(
                "# Заголовок первого уровня\n\n",
                "<h1>Заголовок первого уровня</h1>"
        );
        test(
                "## Второго\n\n",
                "<h2>Второго</h2>"
        );
        test(
                "### Третьего ## уровня\n\n",
                "<h3>Третьего ## уровня</h3>"
        );
        test(
                "#### Четвертого\n# Все еще четвертого\n\n",
                "<h4>Четвертого\n# Все еще четвертого</h4>"
        );
        test(
                "Этот абзац текста,\nсодержит две строки.",
                "<p>Этот абзац текста,\nсодержит две строки.</p>"
        );
        test(
                "    # Может показаться, что это заголовок.\nНо нет, это абзац начинающийся с `#`.\n\n",
                "<p>    # Может показаться, что это заголовок.\nНо нет, это абзац начинающийся с <code>#</code>.</p>"
        );
        test(
                "#И это не заголовок.\n\n",
                "<p>#И это не заголовок.</p>"
        );
        test(
                "###### Заголовки могут быть многострочными\n(и с пропуском заголовков предыдущих уровней)\n\n",
                "<h6>Заголовки могут быть многострочными\n(и с пропуском заголовков предыдущих уровней)</h6>"
        );
        test(
                "Мы все любим *выделять* текст _разными_ способами.\n**Сильное выделение**, используется гораздо реже,\nно __почему бы и нет__?\nНемного --зачеркивания-- еще ни кому не вредило.\nКод представляется элементом `code`.\n\n",
                "<p>Мы все любим <em>выделять</em> текст <em>разными</em> способами.\n<strong>Сильное выделение</strong>, используется гораздо реже,\nно <strong>почему бы и нет</strong>?\nНемного <s>зачеркивания</s> еще ни кому не вредило.\nКод представляется элементом <code>code</code>.</p>"
        );
        test(
                "Обратите внимание, как экранируются специальные\nHTML-символы, такие как `<`, `>` и `&`.\n\n",
                "<p>Обратите внимание, как экранируются специальные\nHTML-символы, такие как <code>&lt;</code>, <code>&gt;</code> и <code>&amp;</code>.</p>"
        );
        test(
                "Экранирование должно работать во всех местах: <>&.\n\n",
                "<p>Экранирование должно работать во всех местах: &lt;&gt;&amp;.</p>"
        );
        test(
                "Знаете ли вы, что в Markdown, одиночные * и _\nне означают выделение?\nОни так же могут быть заэкранированы\nпри помощи обратного слэша: \\*.",
                "<p>Знаете ли вы, что в Markdown, одиночные * и _\nне означают выделение?\nОни так же могут быть заэкранированы\nпри помощи обратного слэша: *.</p>"
        );
        test(
                "\n\n\nЛишние пустые строки должны игнорироваться.\n\n\n\n",
                "<p>Лишние пустые строки должны игнорироваться.</p>"
        );
        test(
                "Любите ли вы *вложеные __выделения__* так,\nкак __--люблю--__ их я?",
                "<p>Любите ли вы <em>вложеные <strong>выделения</strong></em> так,\nкак <strong><s>люблю</s></strong> их я?</p>"
        );

        test("# Заголовок первого уровня\n\n" +
                "## Второго\n\n" +
                "### Третьего ## уровня\n\n" +
                "#### Четвертого\n" +
                "# Все еще четвертого\n\n" +
                "Этот абзац текста,\n" +
                "содержит две строки.\n\n" +
                "    # Может показаться, что это заголовок.\n" +
                "Но нет, это абзац начинающийся с `#`.\n\n" +
                "#И это не заголовок.\n\n" +
                "###### Заголовки могут быть многострочными\n" +
                "(и с пропуском заголовков предыдущих уровней)\n\n" +
                "Мы все любим *выделять* текст _разными_ способами.\n" +
                "**Сильное выделение**, используется гораздо реже,\n" +
                "но __почему бы и нет__?\n" +
                "Немного --зачеркивания-- еще ни кому не вредило.\n" +
                "Код представляется элементом `code`.\n\n" +
                "Обратите внимание, как экранируются специальные\n" +
                "HTML-символы, такие как `<`, `>` и `&`.\n\n" +
                "Знаете ли вы, что в Markdown, одиночные * и _\n" +
                "не означают выделение?\n" +
                "Они так же могут быть заэкранированы\n" +
                "при помощи обратного слэша: \\*.\n\n\n\n" +
                "Лишние пустые строки должны игнорироваться.\n\n" +
                "Любите ли вы *вложеные __выделения__* так,\n" +
                "как __--люблю--__ их я?", "<h1>Заголовок первого уровня</h1>\n" +
                "<h2>Второго</h2>\n" +
                "<h3>Третьего ## уровня</h3>\n" +
                "<h4>Четвертого\n" +
                "# Все еще четвертого</h4>\n" +
                "<p>Этот абзац текста,\n" +
                "содержит две строки.</p>\n" +
                "<p>    # Может показаться, что это заголовок.\n" +
                "Но нет, это абзац начинающийся с <code>#</code>.</p>\n" +
                "<p>#И это не заголовок.</p>\n" +
                "<h6>Заголовки могут быть многострочными\n" +
                "(и с пропуском заголовков предыдущих уровней)</h6>\n" +
                "<p>Мы все любим <em>выделять</em> текст <em>разными</em> способами.\n" +
                "<strong>Сильное выделение</strong>, используется гораздо реже,\n" +
                "но <strong>почему бы и нет</strong>?\n" +
                "Немного <s>зачеркивания</s> еще ни кому не вредило.\n" +
                "Код представляется элементом <code>code</code>.</p>\n" +
                "<p>Обратите внимание, как экранируются специальные\n" +
                "HTML-символы, такие как <code>&lt;</code>, <code>&gt;</code> и <code>&amp;</code>.</p>\n" +
                "<p>Знаете ли вы, что в Markdown, одиночные * и _\n" +
                "не означают выделение?\n" +
                "Они так же могут быть заэкранированы\n" +
                "при помощи обратного слэша: *.</p>\n" +
                "<p>Лишние пустые строки должны игнорироваться.</p>\n" +
                "<p>Любите ли вы <em>вложеные <strong>выделения</strong></em> так,\n" +
                "как <strong><s>люблю</s></strong> их я?</p>\n");

        test("# Без перевода строки в конце", "<h1>Без перевода строки в конце</h1>");
        test("# Один перевод строки в конце\n", "<h1>Один перевод строки в конце</h1>");
        test("# Два перевода строки в конце\n\n", "<h1>Два перевода строки в конце</h1>");
        test(
                "Выделение может *начинаться на одной строке,\n а заканчиваться* на другой",
                "<p>Выделение может <em>начинаться на одной строке,\n а заканчиваться</em> на другой</p>"
        );
        test("# *Выделение* и `код` в заголовках", "<h1><em>Выделение</em> и <code>код</code> в заголовках</h1>");
    }

    protected void addElement(final String tag, final String markup) {
        addElement(tag, markup, markup);
    }

    protected void addElement(final String tag, final String begin, final String end) {
        addElement(tag, begin, (checker, markup, input, output) -> {
            checker.space(input, output);
            input.append(begin);
            open(output, tag);

            checker.word(input, output);
            checker.generate(markup, input, output);
            checker.word(input, output);

            input.append(end);
            close(output, tag);
            checker.space(input, output);
        });
    }

    public void addElement(final String tag, final String begin, final Generator generator) {
        Asserts.assertTrue("Duplicate element " + begin, elements.put(begin, generator) == null);
        tags.computeIfAbsent(tag, k -> new ArrayList<>()).add(begin);
    }

    private final Runner runner = Runner.packages("md2html").files("Md2Html");

    protected Md2HtmlTester test(final String input, final String output) {
        tests.add(Pair.of(input, output));
        return this;
    }

    private static void open(final StringBuilder output, final String tag) {
        output.append("<").append(tag).append(">");
    }

    private static void close(final StringBuilder output, final String tag) {
        output.append("</").append(tag).append(">");
    }

    public void test(final TestCounter counter) {
        counter.scope("Testing " + String.join(", ", tags.keySet()), () -> new Checker(counter).test());
    }

    public class Checker extends BaseChecker {
        public Checker(final TestCounter counter) {
            super(counter);
        }

        protected void test() {
            for (final Pair<String, String> test : tests) {
                test(test);
            }

            for (final String markup : elements.keySet()) {
                randomTest(3, 10, List.of(markup));
            }

            final int d = TestCounter.DENOMINATOR;
            for (int i = 0; i < 10; i++) {
                randomTest(100, 1000, randomMarkup());
            }
            randomTest(100, 100_000 / d, randomMarkup());
        }

        private void test(final Pair<String, String> test) {
            runner.testEquals(counter, Arrays.asList(test.first().split("\n")), Arrays.asList(test.second().split("\n")));
        }

        private List<String> randomMarkup() {
            return Functional.map(tags.values(), random()::randomItem);
        }

        private void randomTest(final int paragraphs, final int length, final List<String> markup) {
            final StringBuilder input = new StringBuilder();
            final StringBuilder output = new StringBuilder();
            emptyLines(input);
            final List<String> markupList = new ArrayList<>(markup);
            for (int i = 0; i < paragraphs; i++) {
                final StringBuilder inputSB = new StringBuilder();
                paragraph(length, inputSB, output, markupList);
                input.append(inputSB);
                emptyLines(input);
            }
            test(Pair.of(input.toString(), output.toString()));
        }

        private void paragraph(final int length, final StringBuilder input, final StringBuilder output, final List<String> markup) {
            final int h = random().nextInt(0, 6);
            final String tag = h == 0 ? "p" : "h" + h;
            if (h > 0) {
                input.append(new String(new char[h]).replace('\0', '#')).append(" ");
            }

            open(output, tag);
            while (input.length() < length) {
                generate(markup, input, output);
                final String middle = random().randomString(ExtendedRandom.ENGLISH);
                input.append(middle).append("\n");
                output.append(middle).append("\n");
            }
            output.setLength(output.length() - 1);
            close(output, tag);

            output.append("\n");
            input.append("\n");
        }

        private void space(final StringBuilder input, final StringBuilder output) {
            if (random().nextBoolean()) {
                final String space = random().nextBoolean() ? " " : "\n";
                input.append(space);
                output.append(space);
            }
        }

        public void generate(final List<String> markup, final StringBuilder input, final StringBuilder output) {
            word(input, output);
            if (markup.isEmpty()) {
                return;
            }
            final String type = random().randomItem(markup);

            markup.remove(type);
            elements.get(type).generate(this, markup, input, output);
            markup.add(type);
        }

        protected void word(final StringBuilder input, final StringBuilder output) {
            final String word = random().randomString(random().randomItem(ExtendedRandom.ENGLISH, ExtendedRandom.GREEK, ExtendedRandom.RUSSIAN));
            input.append(word);
            output.append(word);
        }

        private void emptyLines(final StringBuilder sb) {
            while (random().nextBoolean()) {
                sb.append('\n');
            }
        }
    }

    @FunctionalInterface
    public interface Generator {
        void generate(Checker checker, List<String> markup, StringBuilder input, StringBuilder output);
    }
}
