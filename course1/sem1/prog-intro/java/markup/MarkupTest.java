package markup;

import base.Selector;
import base.TestCounter;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class MarkupTest {
    public static final Selector SELECTOR = new Selector(MarkupTest.class)
            .variant("Base", variant("Markdown", Map.of(
                    "<", "",
                    ">", ""
            )))
            .variant("Html", MarkupTest.variant("Html", Map.of(
                    "*<", "<em>",
                    "*>", "</em>",
                    "__<", "<strong>",
                    "__>", "</strong>",
                    "~<", "<s>",
                    "~>", "</s>"
            )))
            .variant("Tex", MarkupTest.variant("Tex", Map.of(
                    "*<", "\\emph{",
                    "__<", "\\textbf{",
                    "~<", "\\textst{",
                    "*>", "}",
                    "__>", "}",
                    "~>", "}"
            )))

            ;

    public static Consumer<TestCounter> variant(final String name, final Map<String, String> mapping) {
        return MarkupTester.variant(MarkupTest::test, name, mapping);
    }

    private MarkupTest() {
    }

    public static void test(final MarkupTester.Checker checker) {
        checker.test(new Paragraph(List.of(new Text("Hello"))), "Hello");
        checker.test(new Paragraph(List.of(new Emphasis(List.of(new Text("Hello"))))), "*<Hello*>");
        checker.test(new Paragraph(List.of(new Strong(List.of(new Text("Hello"))))), "__<Hello__>");
        checker.test(new Paragraph(List.of(new Strikeout(List.of(new Text("Hello"))))), "~<Hello~>");
        final Paragraph paragraph = new Paragraph(List.of(
                new Strong(List.of(
                        new Text("1"),
                        new Strikeout(List.of(
                                new Text("2"),
                                new Emphasis(List.of(
                                        new Text("3"),
                                        new Text("4")
                                )),
                                new Text("5")
                        )),
                        new Text("6")
                ))
        ));
        checker.test(
                paragraph,
                "__<1~<2*<34*>5~>6__>"
        );
        checker.test(new Paragraph(List.of(
                        new Strong(List.of(new Text("sdq"), new Strikeout(List.of(new Emphasis(List.of(new Text("r"))), new Text("vavc"))), new Text("zg"))))),
                "__<sdq~<*<r*>vavc~>zg__>"
        );
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}
