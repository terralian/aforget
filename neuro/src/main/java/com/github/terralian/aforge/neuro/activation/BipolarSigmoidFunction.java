// AForge Neural Net Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2007-2012
// contacts@aforgenet.com
//
package com.github.terralian.aforge.neuro.activation;

/**
 * Bipolar sigmoid activation function.
 * <p>
 * The class represents bipolar sigmoid activation function with the next expression:
 * 
 * <pre>
 *                 2
 * f(x) = ------------------ - 1
 *        1 + exp(-alpha * x)
 *
 *           2 * alpha * exp(-alpha * x )
 * f'(x) = -------------------------------- = alpha * (1 - f(x)^2) / 2
 *           (1 + exp(-alpha * x))^2
 * </pre>
 * 
 * Output range of the function: <b>[-1, 1]</b>.
 */
public class BipolarSigmoidFunction implements IActivationFunction {

    // sigmoid's alpha value
    private double alpha = 2;

    /**
     * Initializes a new instance of the {@link BipolarSigmoidFunction} class.
     */
    public BipolarSigmoidFunction() {}

    /**
     * Initializes a new instance of the {@link BipolarSigmoidFunction} class.
     * 
     * @param alpha Sigmoid's alpha value.
     */
    public BipolarSigmoidFunction(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public double function(double x) {
        return ((2 / (1 + Math.exp(-alpha * x))) - 1);
    }

    @Override
    public double derivative(double x) {
        double y = function(x);
        return (alpha * (1 - y * y) / 2);
    }

    @Override
    public double derivative2(double y) {
        return (alpha * (1 - y * y) / 2);
    }

    /**
     * Sigmoid's alpha value.
     * <p>
     * The value determines steepness of the function. Increasing value of
     * this property changes sigmoid to look more like a threshold function. Decreasing
     * value of this property makes sigmoid to be very smooth (slowly growing from its
     * minimum value to its maximum value).
     * <p>
     * Default value is set to <b>2</b>.
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * Sigmoid's alpha value.
     * <p>
     * The value determines steepness of the function. Increasing value of
     * this property changes sigmoid to look more like a threshold function. Decreasing
     * value of this property makes sigmoid to be very smooth (slowly growing from its
     * minimum value to its maximum value).
     * <p>
     * Default value is set to <b>2</b>.
     */
    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }
}
