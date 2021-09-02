// AForge Neural Net Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2007-2012
// contacts@aforgenet.com
//
package com.github.terralian.aforge.neuro.layers;

import java.io.Serializable;

import com.github.terralian.aforge.neuro.neurons.Neuron;

/**
 * Base neural layer class.
 * <p>
 * This is a base neural layer class, which represents collection of neurons.
 */
public abstract class Layer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Layer's inputs count.
     */
    protected int inputsCount = 0;

    /**
     * Layer's neurons count.
     */
    protected int neuronsCount = 0;

    /**
     * Layer's neurons.
     */
    protected Neuron[] neurons;

    /**
     * Layer's output vector.
     */
    protected double[] output;

    /**
     * Initializes a new instance of the {@link Layer} class.
     * <p>
     * Protected contructor, which initializes {@link #inputsCount}, {@link #neuronsCount} and {@link #neurons} members.
     * 
     * @param neuronsCount Layer's neurons count.
     * @param inputsCount Layer's inputs count.
     */
    protected Layer(int neuronsCount, int inputsCount) {
        this.inputsCount = Math.max(1, inputsCount);
        this.neuronsCount = Math.max(1, neuronsCount);
        // create collection of neurons
        neurons = new Neuron[this.neuronsCount];
    }

    /**
     * Compute output vector of the layer.<p>
     * The actual layer's output vector is determined by neurons,
     * which comprise the layer - consists of output values of layer's neurons.
     * The output vector is also stored in {@link #output} property.
     * 
     * <note>The method may be called safely from multiple threads to compute layer's
     * output value for the specified input values. However, the value of
     * <see cref="Output"/> property in multi-threaded environment is not predictable,
     * since it may hold layer's output computed from any of the caller threads. Multi-threaded
     * access to the method is useful in those cases when it is required to improve performance
     * by utilizing several threads and the computation is based on the immediate return value
     * of the method, but not on layer's output property.</note>
     * 
     * @param input Input vector.
     * @return Returns layer's output vector.
     */
    public double[] compute(double[] input) {
        // local variable to avoid mutlithread conflicts
        double[] output = new double[neuronsCount];

        // compute each neuron
        for (int i = 0; i < neurons.length; i++)
            output[i] = neurons[i].compute(input);

        // assign output property as well (works correctly for single threaded usage)
        this.output = output;

        return output;
    }

    /**
     * Randomize neurons of the layer. <p>
     * <remarks>Randomizes layer's neurons by calling <see cref="Neuron.Randomize"/> method
     *    of each neuron.</remarks>
     */
    public void randomize() {
        for (Neuron neuron : neurons)
            neuron.randomize();
    }

    /**
     * Layer's inputs count.
     */
    public int getInputsCount() {
        return inputsCount;
    }

    /**
     * Layer's neurons.
     */
    public Neuron[] getNeurons() {
        return neurons;
    }

    /**
     * Layer's output vector.
     * <p>
     * The calculation way of layer's output vector is determined by neurons,
     * which comprise the layer.
     * <p>
     * The property is not initialized (equals to null) until
     * {@link #compute} method is called.
     */
    public double[] getOutput() {
        return output;
    }
}
