// AForge Neural Net Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2007-2012
// contacts@aforgenet.com
//
package com.github.terralian.aforge.neuro.neurons;

/**
 * Distance neuron.
 * <p>
 * Distance neuron computes its output as distance between
 * its weights and inputs - sum of absolute differences between weights'
 * values and corresponding inputs' values. The neuron is usually used in Kohonen
 * Self Organizing Map.
 */
public class DistanceNeuron extends Neuron {

    private static final long serialVersionUID = 1L;

    /**
     * Initializes a new instance of the {@link DistanceNeuron} class.
     * 
     * @param inputs Neuron's inputs count.
     */
    public DistanceNeuron(int inputs) {
        super(inputs);
    }

    /**
     * Computes output value of neuron.
     * 
     * <p>
     * The output value of distance neuron is equal to the distance
     * between its weights and inputs - sum of absolute differences.
     * The output value is also stored in {@link Neuron#output}
     * property.
     * 
     * <p>
     * The method may be called safely from multiple threads to compute neuron's
     * output value for the specified input values. However, the value of
     * {@link Neuron#output} property in multi-threaded environment is not predictable,
     * since it may hold neuron's output computed from any of the caller threads. Multi-threaded
     * access to the method is useful in those cases when it is required to improve performance
     * by utilizing several threads and the computation is based on the immediate return value
     * of the method, but not on neuron's output property.
     * 
     * @throws IllegalArgumentException Wrong length of the input vector, which is not
     * equal to the {@link Neuron#inputsCount} expected value
     */
    @Override
    public double compute(double[] input) {
        // check for corrent input vector
        if (input.length != inputsCount)
            throw new IllegalArgumentException("Wrong length of the input vector.");

        // difference value
        double dif = 0.0;

        // compute distance between inputs and weights
        for (int i = 0; i < inputsCount; i++) {
            dif += Math.abs(weights[i] - input[i]);
        }

        // assign output property as well (works correctly for single threaded usage)
        this.output = dif;

        return dif;
    }

}
