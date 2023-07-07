package jstest.object;

import base.Selector;
import jstest.expression.Builder;
import jstest.expression.OperationsBuilder;
import jstest.functional.FunctionalTest;

import static jstest.expression.Operations.*;

/**
 * Tests for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#js-object-expressions">JavaScript Object Expressions</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ObjectTest {
    /* package-private */
    static Selector.Composite<OperationsBuilder> selector() {
         return Builder.selector(
                ObjectTest.class,
                mode -> false,
                (builder, counter) -> new ObjectTester(
                        counter,
                        builder.language(ObjectTester.OBJECT, FunctionalTest.POLISH),
                        "toString", "parse"
                ),
                "easy", "", "hard", "bonus"
        );
    }

    public static final Selector SELECTOR = selector()
            .variant("Base", ARITH)
            .variant("ExpLn", EXP, LN)
            .variant("ArcTan", ATAN, ATAN2)
            .variant(
                    "Distance",
                    sumsq(2, new int[][]{{1, 1, 1}, {1, 5, 1}, {5, 1, 1}, {5, 5, 1}, {9, 9, 17}, {17, 14, 9}, {22, 22, 17}, {22, 22, 17}}),
                    sumsq(3, new int[][]{{1, 1, 1}, {5, 5, 1}, {1, 1, 5}, {5, 5, 1}, {5, 5, 1}, {25, 21, 30}, {110, 106, 61}}),
                    sumsq(4, new int[][]{{1, 1, 1}, {5, 5, 5}, {1, 1, 5}, {5, 5, 5}, {5, 5, 5}, {5, 1, 1}, {58, 21, 30}, {182, 116, 125}}),
                    sumsq(5, new int[][]{{1, 1, 1}, {5, 5, 21}, {1, 1, 29}, {5, 5, 1}, {5, 5, 5}, {1, 1, 5}, {5, 1, 5}, {1, 1, 1}, {5, 1, 1}, {1, 13, 1}, {1, 1, 1}, {13, 1, 13}}),
                    distance(2, new int[][]{{1, 1, 1}, {1, 17, 1}, {17, 1, 1}, {17, 17, 1}, {32, 32, 40}, {40, 37, 32}, {68, 68, 48}, {68, 68, 48}}),
                    distance(3, new int[][]{{1, 1, 1}, {19, 19, 1}, {1, 1, 19}, {19, 19, 1}, {19, 19, 1}, {77, 73, 82}, {403, 399, 184}}),
                    distance(4, new int[][]{{1, 1, 1}, {21, 21, 21}, {1, 1, 21}, {21, 21, 21}, {21, 21, 21}, {21, 1, 1}, {154, 85, 94}, {722, 509, 518}}),
                    distance(5, new int[][]{{1, 1, 1}, {49, 23, 49}, {23, 23, 23}, {1, 1, 49}, {49, 23, 23}, {1, 23, 1}, {1, 1, 23}, {49, 23, 1}, {1, 75, 1}, {1, 1, 23}, {1, 1, 1}, {49, 1, 23}})
            )
            .variant(
                    "SumrecHMean",
                    sumrec(2, new int[][]{{1, 1, 1}, {1, 10, 1}, {10, 1, 1}, {10, 10, 1}, {18, 18, 29}, {29, 23, 18}, {43, 43, 29}, {43, 43, 29}}),
                    sumrec(3, new int[][]{{1, 1, 1}, {16, 16, 3}, {3, 3, 16}, {16, 16, 3}, {10, 10, 1}, {43, 39, 48}, {210, 206, 152}}),
                    sumrec(4, new int[][]{{1, 1, 1}, {16, 16, 16}, {3, 3, 16}, {16, 16, 16}, {10, 10, 10}, {10, 1, 1}, {85, 39, 48}, {326, 226, 235}}),
                    sumrec(5, new int[][]{{3, 3, 3}, {10, 10, 36}, {1, 1, 49}, {10, 10, 1}, {10, 10, 10}, {3, 3, 16}, {16, 3, 16}, {1, 1, 1}, {22, 3, 3}, {1, 23, 1}, {3, 3, 3}, {23, 1, 23}}),
                    hmean(2, new int[][]{{1, 1, 1}, {1, 73, 1}, {73, 1, 1}, {73, 73, 1}, {103, 103, 114}, {114, 108, 103}, {199, 199, 124}, {199, 199, 124}}),
                    hmean(3, new int[][]{{1, 1, 1}, {77, 77, 1}, {1, 1, 77}, {77, 77, 1}, {77, 77, 1}, {225, 221, 230}, {985, 981, 446}}),
                    hmean(4, new int[][]{{1, 1, 1}, {47, 47, 47}, {1, 1, 47}, {47, 47, 47}, {47, 47, 47}, {47, 1, 1}, {292, 177, 186}, {1322, 945, 954}}),
                    hmean(5, new int[][]{{3, 3, 3}, {165, 81, 165}, {81, 81, 81}, {1, 1, 165}, {165, 81, 81}, {1, 81, 1}, {1, 1, 81}, {165, 81, 1}, {1, 249, 1}, {1, 1, 81}, {3, 3, 3}, {165, 1, 81}})
            )
            .selector();

    private ObjectTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}
