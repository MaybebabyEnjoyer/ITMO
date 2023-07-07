package expression.generic;

import expression.generic.parser.GenericParser;
import expression.exceptions.ExpressionException;
import expression.generic.parser.expressions.GenericExpression;
import expression.generic.parser.type.*;

import java.util.HashMap;
import expression.generic.factory.*;

public class GenericTabulator implements Tabulator {


    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
        if (FactoryProd.getFactory(mode) == null) {
            throw new Exception("Unknown mode: " + mode);
        }
        return table(FactoryProd.getFactory(mode).createType(), expression, x1, x2, y1, y2, z1, z2);
    }

    private <T> Object[][][] table(TypeInterface<T> typeInterface, String expression, int x1, int x2, int y1, int y2, int z1, int z2) {
        GenericParser<T> parser = new GenericParser<>(typeInterface);
        GenericExpression<T> parsedExpression = parser.parse(expression);
        Object[][][] result = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];
        for (int i = 0; i < x2 - x1 + 1; i++) {
            for (int j = 0; j < y2 - y1 + 1; j++) {
                for (int k = 0; k < z2 - z1 + 1; k++) {
                    try {
                        result[i][j][k] = parsedExpression.evaluate(typeInterface.valueOf((x1 + i)), typeInterface.valueOf((y1 + j)),
                                typeInterface.valueOf((z1 + k)));
                    } catch (ExpressionException ignored) {
                    }
                }
            }
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        String mode = args[0].substring(1);
        String expression = args[1];
        GenericTabulator tabulator = new GenericTabulator();
        tabulator.tabulate(mode, expression, -2, 2, -2, 2, -2, 2);
        for(int i = 0; i < 5; i++) {
            for (int j =0; j < 5; j++) {
                for (int k = 0; k < 5; k++) {
                    System.out.println(tabulator.tabulate(mode, expression, -2, 2, -2, 2, -2, 2)[i][j][k]);
                }
            }
        }
    }
}
