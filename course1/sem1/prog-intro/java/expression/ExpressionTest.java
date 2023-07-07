package expression;

import base.Selector;
import base.TestCounter;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ExpressionTest {
    public static final Selector SELECTOR = new Selector(ExpressionTest.class, "easy", "hard")
            .variant("Base", v(Expression::tester))
            .variant("Triple", v(TripleExpression::tester))
            .variant("Double", v(DoubleExpression::tester))
            ;

    private ExpressionTest() {
    }

    public static Consumer<TestCounter> v(final Function<TestCounter, ? extends ExpressionTester<?, ?>> tester) {
        return t -> tester.apply(t).test();
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}
