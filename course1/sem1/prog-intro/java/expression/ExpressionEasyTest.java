package expression;

import base.Selector;
import base.TestCounter;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ExpressionEasyTest {
    public static final Selector SELECTOR = new Selector(ExpressionEasyTest.class, "easy", "hard")
            .variant("Base", v(Expression::tester))
            .variant("Triple", v(TripleExpression::tester))
            ;

    private ExpressionEasyTest() {
    }

    public static Consumer<TestCounter> v(final Function<TestCounter, ? extends ExpressionTester<?, ?>> tester) {
        return t -> tester.apply(t).test();
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}
