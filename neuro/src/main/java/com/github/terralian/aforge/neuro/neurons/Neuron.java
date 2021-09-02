// AForge Neural Net Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2007-2012
// contacts@aforgenet.com
//
package com.github.terralian.aforge.neuro.neurons;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

import com.github.terralian.aforge.core.Range;

/**
 * Base neuron class.
 * <p>
 * This is a base neuron class, which encapsulates such 
 * common properties, like neuron's input, output and weights.
 */
public abstract class Neuron implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Neuron's inputs count.
     */
    protected int inputsCount = 0;

    /**
     * Nouron's wieghts
     */
    protected double[] weights = null;

    /**
     * Neuron's output value.
     */
    protected double output = 0;

    /**
     * Random number generator.
     * <p>
     * The generator is used for neuron's weights randomization.
     * <p>
     * source c# code: {@code ThreadSafeRandom rand = new ThreadSafeRandom( );}
     */
    protected static ThreadLocalRandom rand = ThreadLocalRandom.current();

    /**
     * Random generator range
     * <p>
     * The property allows to initialize random generator with a custom seed. The generator is used for neuron's weights randomization.
     */
    protected static Range randRange = new Range(0.0f, 1.0f);

    /**
     * Initializes a new instance of the {@link Neuron} class.
     * <p>
     * The new neuron will be randomized (see {@link #randomize()}) after it is created.
     * 
     * @param inputs Neuron's inputs count.
     */
    protected Neuron(int inputs) {
        // allocate weights
        inputsCount = Math.max(1, inputs);
        weights = new double[inputsCount];
        // randomize the neuron
        randomize();
    }

    /**
     * Randomize neuron.
     * <p>
     * Initialize neuron's weights with random values within the range specified by {@link Neuron#randRange}
     */
    public void randomize() {
        double d = randRange.length();
        // randomize weights
        for ( int i = 0; i < inputsCount; i++ )
            weights[i] = rand.nextDouble() * d + randRange.getMin();
    }

    /**
     * Computes output value of neuron.
     * <p>
     * The actual neuron's output value is determined by inherited class. The output value is also stored in Output property.
     * 
     * @param input Input vector.
     * @return Returns neuron's output value.
     */
    public abstract double compute(double[] input);

    /**
     * Random number generator.
     * <p>
     * The property allows to initialize random generator with a custom seed. The generator is
     * used for neuron's weights randomization.
     */
    public static ThreadLocalRandom getRandGenerator() {
        return rand;
    }

    /**
     * Random number generator.
     * <p>
     * The property allows to initialize random generator with a custom seed. The generator is
     * used for neuron's weights randomization.
     */
    public static void setRandGenerator(ThreadLocalRandom rand) {
        Neuron.rand = rand;
    }

    /**
     * Random generator range.
     * <p>
     * Sets the range of random generator. Affects initial values of neuron's weight.
     * Default value is [0, 1].
     */
    public static Range getRandRange() {
        return randRange;
    }

    /**
     * Random generator range.
     * <p>
     * Sets the range of random generator. Affects initial values of neuron's weight.
     * Default value is [0, 1].
     */
    public static void setRandRange(Range randRange) {
        Neuron.randRange = randRange;
    }

    /**
     * Neuron's inputs count
     */
    public int getInputsCount() {
        return inputsCount;
    }

    /**
     * Neuron's output value.
     * <p>
     * The calculation way of neuron's output value is determined by inherited class.
     */
    public double getOutput() {
        return output;
    }

    /**
     * Neuron's weights.
     */
    public double[] getWeights() {
        return weights;
    }
}
