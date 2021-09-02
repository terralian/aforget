package com.github.terralian.aforge.neuro.activation;

/**
 * Threshold activation function.
 * <p>
 * The class represents threshold activation function with the next expression:
 * 
 * <pre>
 * f(x) = 1, if x >= 0, otherwise 0
 * </pre>
 * 
 * Output range of the function: <b>[0, 1]</b>.
 */
public class ThresholdFunction implements IActivationFunction {

    @Override
    public double function(double x) {
        return (x >= 0) ? 1 : 0;
    }

    /**
     * Calculates function derivative (not supported).
     * <p>
     * The method is not supported, because it is not possible to calculate derivative of the function.
     * 
     * @return Always returns 0.
     */
    @Override
    public double derivative(double x) {
        return 0;
    }

    /**
     * Calculates function derivative (not supported).
     * <p>
     * The method is not supported, because it is not possible to calculate derivative of the function.
     * 
     * @return Always returns 0.
     */
    @Override
    public double derivative2(double y) {
        return 0;
    }
}
