package markup;

import base.Asserts;
import base.Selector;
import base.TestCounter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class MarkupListTest {
    public static final Selector SELECTOR = new Selector(MarkupListTest.class)
            .variant("HtmlList", variant("Html", Map.ofEntries()))
            .variant("TexList", MarkupListTest.variant("Tex", Map.ofEntries(
                    Map.entry("<em>", "\\emph{"),
                    Map.entry("</em>", "}"),
                    Map.entry("<strong>", "\\textbf{"),
                    Map.entry("</strong>", "}"),
                    Map.entry("<s>", "\\textst{"),
                    Map.entry("</s>", "}"),
                    Map.entry("<ul>", "\\begin{itemize}"),
                    Map.entry("</ul>", "\\end{itemize}"),
                    Map.entry("<ol>", "\\begin{enumerate}"),
                    Map.entry("</ol>", "\\end{enumerate}"),
                    Map.entry("<li>", "\\item "),
                    Map.entry("</li>", "")
            )))
            ;

    private MarkupListTest() {
    }

    public static Consumer<TestCounter> variant(final String name, final Map<String, String> mapping) {
        return MarkupTester.variant(MarkupListTest::test, name, mapping);
    }

    private static void test(final MarkupTester.Checker checker) {
        final Paragraph paragraph1 = new Paragraph(List.of(
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
        final String paragraph1Markup = "<strong>1<s>2<em>34</em>5</s>6</strong>";

        final Paragraph paragraph2 = new Paragraph(List.of(new Strong(List.of(
                new Text("sdq"),
                new Strikeout(List.of(new Emphasis(List.of(new Text("r"))), new Text("vavc"))),
                new Text("zg")))
        ));
        final String paragraph2Markup = "<strong>sdq<s><em>r</em>vavc</s>zg</strong>";

        checker.test(paragraph1, paragraph1Markup);
        checker.test(paragraph2, paragraph2Markup);

        final ListItem li1 = new ListItem(List.of(new Paragraph(List.of(new Text("1.1"))), new Paragraph(List.of(new Text("1.2")))));
        final ListItem li2 = new ListItem(List.of(new Paragraph(List.of(new Text("2")))));
        final ListItem pli1 = new ListItem(List.of(paragraph1));
        final ListItem pli2 = new ListItem(List.of(paragraph2));

        final ListItem nestedUl = new ListItem(List.of(ul(li1, li2)));
        final String nestedUlMarkup = ul("1.11.2", "2");

        checker.test(ul(li1), ul("1.11.2"));
        checker.test(ul(li2), ul("2"));
        checker.test(ul(pli1), ul(paragraph1Markup));
        checker.test(ul(pli2), ul(paragraph2Markup));
        checker.test(ul(li1, li2), nestedUlMarkup);
        checker.test(ul(pli1, pli2), ul(paragraph1Markup, paragraph2Markup));
        checker.test(ul(nestedUl), ul(nestedUlMarkup));

        final ListItem nestedOl = new ListItem(List.of(ol(li1, li2)));
        final String nestedOlMarkup = ol("1.11.2", "2");
        checker.test(ol(li1), ol("1.11.2"));
        checker.test(ol(li2), ol("2"));
        checker.test(ol(pli1), ol(paragraph1Markup));
        checker.test(ol(pli2), ol(paragraph2Markup));
        checker.test(ol(li1, li2), nestedOlMarkup);
        checker.test(ol(pli1, pli2), ol(paragraph1Markup, paragraph2Markup));
        checker.test(ol(nestedOl), ol(nestedOlMarkup));

        checker.test(ul(nestedUl, nestedOl), ul(nestedUlMarkup, nestedOlMarkup));
        checker.test(ol(nestedUl, nestedOl), ol(nestedUlMarkup, nestedOlMarkup));

        checker.test(
                ul(nestedUl, nestedOl, pli1, pli2),
                ul(nestedUlMarkup, nestedOlMarkup, paragraph1Markup, paragraph2Markup)
        );
        checker.test(
                ol(nestedUl, nestedOl, pli1, pli2),
                ol(nestedUlMarkup, nestedOlMarkup, paragraph1Markup, paragraph2Markup)
        );

        checkTypes();
    }

    private static OrderedList ol(final ListItem... items) {
        return new OrderedList(List.of(items));
    }

    private static String ol(final String... items) {
        return list("ol", items);
    }

    private static UnorderedList ul(final ListItem... items) {
        return new UnorderedList(List.of(items));
    }

    private static String ul(final String... items) {
        return list("ul", items);
    }

    private static String list(final String type, final String[] items) {
        return "<" + type + ">" + Stream.of(items).map(item -> "<li>" + item + "</li>").collect(Collectors.joining()) + "</" + type + ">";
    }

    private static Class<?> loadClass(final String name) {
        try {
            return Class.forName(name);
        } catch (final ClassNotFoundException e) {
            throw Asserts.error("Cannot find class %s: %s", name, e);
        }
    }

    private static Map<String, Class<?>> loadClasses(final String... names) {
        return Arrays.stream(names)
                .collect(Collectors.toUnmodifiableMap(Function.identity(), name -> loadClass("markup." + name)));
    }

    private static void checkTypes() {
        final Map<String, Class<?>> classes = loadClasses("Text", "Emphasis", "Strikeout", "Strong", "Paragraph", "OrderedList", "UnorderedList", "ListItem");
        final String[] inlineClasses = {"Text", "Emphasis", "Strikeout", "Strong"};
        final Set<Class<?>> allClasses = Set.copyOf(classes.values());

        checkConstructor(classes, allClasses, "OrderedList", "ListItem");
        checkConstructor(classes, allClasses, "UnorderedList", "ListItem");
        checkConstructor(classes, allClasses, "ListItem", "OrderedList", "UnorderedList", "Paragraph");
        List.of("Paragraph", "Emphasis", "Strong", "Strikeout")
                .forEach(parent -> checkConstructor(classes, allClasses, parent, inlineClasses));
        checkConstructor(classes, allClasses, "ListItem", "Paragraph", "OrderedList", "UnorderedList");
    }

    private static void checkConstructor(final Map<String, Class<?>> classes, final Set<Class<?>> allClasses, final String parent, final String... children) {
        try {
            final Class<?> parentC = classes.get(parent);
            final Type argType = parentC.getConstructor(List.class).getGenericParameterTypes()[0];
            if (argType instanceof ParameterizedType) {
                final Type actualType = ((ParameterizedType) argType).getActualTypeArguments()[0];
                if (actualType instanceof Class) {
                    final Predicate<Class<?>> isAssignableFrom = ((Class<?>) actualType)::isAssignableFrom;
                    final Set<Class<?>> childC = Arrays.stream(children).map(classes::get).collect(Collectors.toUnmodifiableSet());
                    checkType(parentC, Predicate.not(isAssignableFrom), "not ", childC.stream());
                    checkType(parentC, isAssignableFrom, "", allClasses.stream().filter(Predicate.not(childC::contains)));
                }
            }
        } catch (final NoSuchMethodException e) {
            throw Asserts.error("Missing %s(List<...>) constructor: %s", parent, e);
        }
    }

    private static void checkType(final Class<?> parent, final Predicate<Class<?>> predicate, final String not, final Stream<Class<?>> children) {
        children.filter(predicate).findAny().ifPresent(child -> {
            throw Asserts.error("%s is %scompatible with child of type %s", parent, not, child);
        });
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}
