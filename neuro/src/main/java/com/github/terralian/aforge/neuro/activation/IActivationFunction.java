// AForge Neural Net Library
// AForge.NET framework
//
// Copyright � Andrew Kirillov, 2005-2008
// andrew.kirillov@gmail.com
//
package com.github.terralian.aforge.neuro.activation;

/**
 * Activation function interface.
 * <p>
 * All activation functions, which are supposed to be used with
 * neurons, which calculate their output as a function of weighted sum of
 * their inputs, should implement this interfaces.
 */
public interface IActivationFunction {

    /**
     * Calculates function value.
     * <p>
     * The method calculates function value at point <paramref name="x"/>.
     * 
     * @param x Function input value.
     * @return Function output value, <i>f(x)</i>.
     */
    double function(double x);

    /**
     * Calculates function derivative.
     * <p>
     * The method calculates function derivative at point <paramref name="x"/>.
     * 
     * @param x Function input value.
     * @return Function derivative, <i>f'(x)</i>.
     */
    double derivative(double x);

    /**
     * Calculates function derivative.
     * <p>
     * The method calculates the same derivative value as the
     * {@link #derivative} method, but it takes not the input <b>x</b> value
     * itself, but the function value, which was calculated previously with
     * the help of {@link #function} method.
     * <p>
     * Some applications require as function value, as derivative value,
     * so they can save the amount of calculations using this method to calculate derivative.
     * 
     * @param y Function output value - the value, which was obtained with the help of {@link #function(double)} method.
     * @return Function derivative, <i>f'(x)</i>.
     */
    double derivative2(double y);
}
