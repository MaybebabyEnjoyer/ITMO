package expression.exceptions;

import expression.TripleExpression;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        ExpressionParser expressionParser = new ExpressionParser();
        TripleExpression expression = expressionParser.parse(sc.nextLine());
        System.out.println(expression);
        System.out.println(expression.evaluate(10000, 100000, 10000));
    }
}