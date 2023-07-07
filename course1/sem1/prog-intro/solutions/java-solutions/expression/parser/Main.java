package expression.parser;

import expression.BaseExpression;
import expression.TripleExpression;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ExpressionParser expressionParser = new ExpressionParser();
        BaseExpression expression = expressionParser.parse(sc.nextLine());
        System.out.println(expression);
        System.out.println(expression.evaluate(1, 1, 1));
    }
}
