package md2html;

import base.Selector;

import java.util.function.Consumer;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class Md2HtmlTest {

    private static final Consumer<? super Md2HtmlTester> IMAGE = tester -> tester
            .test("![картинок](http://www.ifmo.ru/images/menu/small/p10.jpg)", "<p><img alt='картинок' src='http://www.ifmo.ru/images/menu/small/p10.jpg'></p>")
            .test("![картинка](https://kgeorgiy.info)", "<p><img alt='картинка' src='https://kgeorgiy.info'></p>")
            .test("![картинка с __псевдо-выделением__](https://kgeorgiy.info)", "<p><img alt='картинка с __псевдо-выделением__' src='https://kgeorgiy.info'></p>")
            .addElement("img", "![", (checker, markup, input, output) -> {
                final StringBuilder alt = new StringBuilder();
                final StringBuilder src = new StringBuilder();

                checker.generate(markup, alt, new StringBuilder());
                checker.generate(markup, src, new StringBuilder());

                input.append("![").append(alt).append("](").append(src).append(')');
                output.append("<img alt='").append(alt).append("' src='").append(src).append("'>");
            });

    private static final Consumer<? super Md2HtmlTester> LINK = tester -> tester
            .test("[ссылок с _выделением_](https://kgeorgiy.info)", "<p><a href='https://kgeorgiy.info'>ссылок с <em>выделением</em></a></p>")
            .test("[ссылка с __выделением__](https://kgeorgiy.info)", "<p><a href='https://kgeorgiy.info'>ссылка с <strong>выделением</strong></a></p>")
            .test("[ссылка без выделения](https://kgeorgiy.info)", "<p><a href='https://kgeorgiy.info'>ссылка без выделения</a></p>")
            .test("[ссылка без выделения](https://hello__kgeorgiy.info)", "<p><a href='https://hello__kgeorgiy.info'>ссылка без выделения</a></p>")
            .test("_выделение [ссылка с __выделением__](https://kgeorgiy.info)_", "<p><em>выделение <a href='https://kgeorgiy.info'>ссылка с <strong>выделением</strong></a></em></p>")
            .addElement("a", "[", (checker, markup, input, output) -> {
                final StringBuilder in = new StringBuilder();
                final StringBuilder out = new StringBuilder();
                checker.generate(markup, in, out);

                final StringBuilder href = new StringBuilder();
                checker.generate(markup, href, new StringBuilder());

                input.append("[").append(in).append("](").append(href).append(')');
                output.append("<a href='").append(href).append("'>").append(out).append("</a>");
            });

    private static final Consumer<? super Md2HtmlTester> MARK = tester -> tester
            .test("~выделение~", "<p><mark>выделение</mark></p>")
            .test("Это ~выделение~, вставленное в текст", "<p>Это <mark>выделение</mark>, вставленное в текст</p>")
            .test("Это не \\~выделение\\~", "<p>Это не ~выделение~</p>")
            .addElement("mark", "~");

    private static final Consumer<Md2HtmlTester> UNDERLINE = tester -> tester
            .test("++подчеркивание++", "<p><u>подчеркивание</u></p>")
            .test("Это ++подчеркивание++, вложенное в текст", "<p>Это <u>подчеркивание</u>, вложенное в текст</p>")
            .test("Это не \\++подчеркивание+\\+", "<p>Это не ++подчеркивание++</p>")
            .addElement("u", "++");

    public static final Selector SELECTOR = Selector.composite(Md2HtmlTest.class, c -> new Md2HtmlTester(), Md2HtmlTester::test)
            .variant("Base")
            .variant("Image", IMAGE)
            .variant("Link", LINK)
            .variant("Mark", MARK)
            .variant("Underline", UNDERLINE)

            .selector();

    private Md2HtmlTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}
