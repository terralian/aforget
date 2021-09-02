// AForge Neural Net Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2007-2012
// contacts@aforgenet.com
//
package com.github.terralian.aforge.neuro.neurons;

import com.github.terralian.aforge.neuro.activation.IActivationFunction;

/**
 * Activation neuron.
 * <p>
 * Activation neuron computes weighted sum of its inputs, adds
 *  threshold value and then applies activation function
 * The neuron isusually used in multi-layer neural networks.
 */
public class ActivationNeuron extends Neuron {

    private static final long serialVersionUID = 1L;

    /**
     * Threshold value.
     * <p>
     * The value is added to inputs weighted sum before it is passed to activation function.
     */
    protected double threshold = 0.0;

    /**
     * Activation function.
     * <p>
     * The function is applied to inputs weighted sum plus threshold value.
     */
    protected IActivationFunction function = null;

    /**
     * Initializes a new instance of the {@link ActivationNeuron} class.
     * 
     * @param inputs Neuron's inputs count.
     * @param function Neuron's activation function.
     */
    public ActivationNeuron(int inputs, IActivationFunction function) {
        super(inputs);
        this.function = function;
    }

    @Override
    public void randomize() {
        // randomize weights
        super.randomize();
        // randomize threshold
        threshold = rand.nextDouble() * (randRange.length()) + randRange.getMin();
    }

    /**
     * Computes output value of neuron.
     * <p>
     * The output value of activation neuron is equal to value
     * of nueron's activation function, which parameter is weighted sum
     * of its inputs plus threshold value. The output value is also stored
     * in {@link Neuron#output} property.
     * <p>
     * The method may be called safely from multiple threads to compute neuron's
     * output value for the specified input values. However, the value of
     * {@link Neuron#output} property in multi-threaded environment is not predictable,
     * since it may hold neuron's output computed from any of the caller threads. Multi-threaded
     * access to the method is useful in those cases when it is required to improve performance
     * by utilizing several threads and the computation is based on the immediate return value
     * of the method, but not on neuron's output property.
     * 
     * @throws IllegalArgumentException Wrong length of the input vector, which is not equal to the {@linK Neuron#inputsCount} expected value
     */
    @Override
    public double compute(double[] input) {
        // check for corrent input vector
        if (input.length != inputsCount)
            throw new IllegalArgumentException("Wrong length of the input vector.");

        // initial sum value
        double sum = 0.0;

        // compute weighted sum of inputs
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i] * input[i];
        }
        sum += threshold;

        // local variable to avoid mutlithreaded conflicts
        double output = function.function(sum);
        // assign output property as well (works correctly for single threaded usage)
        this.output = output;

        return output;
    }

    /**
     * Threshold value.
     * <p>
     * The value is added to inputs weighted sum before it is passed to activation
     * function.
     */
    public double getThreshold() {
        return threshold;
    }

    /**
     * Threshold value.
     * <p>
     * The value is added to inputs weighted sum before it is passed to activation
     * function.
     */
    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    /**
     * Neuron's activation function.
     */
    public IActivationFunction getActivationFunction() {
        return function;
    }

    /**
     * Neuron's activation function.
     */
    public void setActivationFunction(IActivationFunction function) {
        this.function = function;
    }

}
