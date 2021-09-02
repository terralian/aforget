// AForge Neural Net Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2007-2012
// contacts@aforgenet.com
//
package com.github.terralian.aforge.neuro.networks;

import com.github.terralian.aforge.neuro.activation.IActivationFunction;
import com.github.terralian.aforge.neuro.layers.ActivationLayer;
import com.github.terralian.aforge.neuro.neurons.ActivationNeuron;

/**
 * Activation network.
 * <p>
 * Activation network is a base for multi-layer neural network 
 * with activation functions. It consists of {@link ActivationLayer}.
 * <p>
 * Sample usage:
 * <pre>
 * // create activation network
 * ActivationNetwork network = new ActivationNetwork(
 *     new SigmoidFunction( ), // sigmoid activation function
 *     3,                      // 3 inputs
 *     4, 1 );                 // 2 layers:
 *                             // 4 neurons in the firs layer
 *                             // 1 neuron in the second layer
 * </pre>
 */
public class ActivationNetwork extends Network {

    private static final long serialVersionUID = 1L;

    /**
     * Initializes a new instance of the {@link ActivationNetwork} class.
     * <p>
     * The new network is randomized (see {@link ActivationNeuron#randomize()} method) after it is created.
     * 
     * @param function Activation function of neurons of the network.
     * @param inputsCount Network's inputs count.
     * @param neuronsCount Array, which specifies the amount of neurons in each layer of the neural network.
     */
    public ActivationNetwork(IActivationFunction function, int inputsCount, int... neuronsCount) {
        super(inputsCount, neuronsCount.length);
        // create each layer
        for (int i = 0; i < layers.length; i++) {
            layers[i] = new ActivationLayer(
                    // neurons count in the layer
                    neuronsCount[i],
                    // inputs count of the layer
                    (i == 0) ? inputsCount : neuronsCount[i - 1],
                    // activation function of the layer
                    function);
        }
    }

    /**
     * Set new activation function for all neurons of the network.
     * <p>
     * The method sets new activation function for all neurons by calling
     * {@link ActivationLayer#setActivationFunction(IActivationFunction)} method for each layer of the network.
     * 
     * @param function Activation function to set.
     */
    public void setActivationFunction(IActivationFunction function) {
        for (int i = 0; i < layers.length; i++) {
            ((ActivationLayer) layers[i]).setActivationFunction(function);
        }
    }
}
