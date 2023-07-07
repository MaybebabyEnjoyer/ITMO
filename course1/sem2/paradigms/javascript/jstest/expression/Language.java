package jstest.expression;

import jstest.expression.BaseTester.Test;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Expression language.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class Language {
    private final Dialect parsed;
    private final Dialect unparsed;
    private final BaseVariant variant;
    private final List<Test> tests;

    public Language(final Dialect parsed, final Dialect unparsed, final BaseVariant variant) {
        this.parsed = parsed;
        this.unparsed = unparsed;

        this.variant = variant;
        tests = variant.getTests().stream().map(this::test).collect(Collectors.toUnmodifiableList());
    }

    private Test test(final Expr expr) {
        return new Test(expr, parsed.render(expr), unparsed.render(expr));
    }

    public Test randomTest(final int size) {
        return test(variant.randomTest(size));
    }

    public List<int[]> getSimplifications() {
        return variant.getSimplifications();
    }

    public List<Expr> getVariables() {
        return variant.getVariables();
    }

    public List<Test> getTests() {
        return tests;
    }
}
