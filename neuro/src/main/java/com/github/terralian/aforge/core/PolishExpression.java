// AForge Core Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � Andrew Kirillov, 2007-2009
// andrew.kirillov@aforgenet.com
//
package com.github.terralian.aforge.core;

import java.util.Stack;

// Quick and dirty implementation of polish expression evaluator

/**
 * Evaluator of expressions written in reverse polish notation.
 * <p>
 * The class evaluates expressions writen in reverse postfix polish notation.
 * <p>
 * The list of supported functuins is:
 * <ul>
 * <li><b>Arithmetic functions</b>: +, -, *, /;
 * <li><b>sin</b> - sine;
 * <li><b>cos</b> - cosine;
 * <li><b>ln</b> - natural logarithm;
 * <li><b>exp</b> - exponent;
 * <li><b>sqrt</b> - square root.
 * </ul>
 * <p>
 * Arguments for these functions could be as usual constants, written as numbers, as variables, writen as $&lt;var_number&gt; (<b>$2</b>,
 * for example). The variable number is zero based index of variables array.
 * <p>
 * Sample usage:
 * 
 * <pre>
 * // expression written in polish notation
 * string expression = "2 $0 / 3 $1 * +";
 * // variables for the expression
 * double[] vars = new double[] {3, 4};
 * // expression evaluation
 * double result = PolishExpression.Evaluate(expression, vars);
 * </pre>
 */
public final class PolishExpression {

    /**
     * Evaluates specified expression.
     * 
     * @param expression Expression written in postfix polish notation.
     * @param variables Variables for the expression.
     * @return Evaluated value of the expression.
     * @throws IllegalArgumentException Unsupported function is used in the expression.
     * @throws IllegalArgumentException Incorrect postfix polish expression.
     */
    public static double evaluate(String expression, double[] variables) {
        // split expression to separate tokens, which represent functions ans variables
        String[] tokens = expression.trim().split(" ");
        // arguments stack
        Stack<Double> arguments = new Stack<>();

        // walk through all tokens
        for (String token : tokens)
        {
            // check for token type
            if (Character.isDigit(token.charAt(0))) {
                // the token in numeric argument
                arguments.push(Double.parseDouble(token));
            } else if (token.charAt(0) == '$') {
                // the token is variable
                arguments.push(variables[Integer.parseInt(token.substring(1))]);
            } else
            {
                // each function has at least one argument, so let's get the top one
                // argument from stack
                double v = (double) arguments.pop();

                // check for function
                switch (token)
                {
                    case "+": // addition
                        arguments.push((double) arguments.pop() + v);
                        break;

                    case "-": // subtraction
                        arguments.push((double) arguments.pop() - v);
                        break;

                    case "*": // multiplication
                        arguments.push((double) arguments.pop() * v);
                        break;

                    case "/": // division
                        arguments.push((double) arguments.pop() / v);
                        break;

                    case "sin": // sine
                        arguments.push(Math.sin(v));
                        break;

                    case "cos": // cosine
                        arguments.push(Math.cos(v));
                        break;

                    case "ln": // natural logarithm
                        arguments.push(Math.log(v));
                        break;

                    case "exp": // exponent
                        arguments.push(Math.exp(v));
                        break;

                    case "sqrt": // square root
                        arguments.push(Math.sqrt(v));
                        break;

                    default:
                        // throw exception informing about undefined function
                        throw new IllegalArgumentException("Unsupported function: " + token);
                }
            }
        }

        // check stack size
        if (arguments.size() != 1) {
            throw new IllegalArgumentException("Incorrect expression.");
        }

        // return the only value from stack
        return (double) arguments.pop();
    }
}
