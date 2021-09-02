// AForge Neural Net Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2007-2012
// contacts@aforgenet.com
//
package com.github.terralian.aforge.neuro.layers;

import com.github.terralian.aforge.neuro.activation.IActivationFunction;
import com.github.terralian.aforge.neuro.neurons.ActivationNeuron;

/**
 * Activation layer.
 * <p>
 * Activation layer is a layer of {@link ActivationNeuron}. The layer is usually used in multi-layer neural networks.
 */
public class ActivationLayer extends Layer {

    private static final long serialVersionUID = 1L;

    /**
     * Initializes a new instance of the {@link ActivationLayer} class.
     * <p>
     * The new layer is randomized (see {@link ActivationNeuron#randomize()} method) after it is created.
     * 
     * @param neuronsCount Layer's neurons count.
     * @param inputsCount Layer's inputs count.
     * @param function Activation function of neurons of the layer.
     */
    public ActivationLayer(int neuronsCount, int inputsCount, IActivationFunction function) {
        super(neuronsCount, inputsCount);
        for (int i = 0; i < neurons.length; i++)
            neurons[i] = new ActivationNeuron(inputsCount, function);
    }

    /**
     * Set new activation function for all neurons of the layer.
     * <p>
     * The methods sets new activation function for each neuron by setting their {@link ActivationNeuron#function} property.
     * 
     * @param function Activation function to set.
     */
    public void setActivationFunction(IActivationFunction function) {
        for (int i = 0; i < neurons.length; i++) {
            ((ActivationNeuron) neurons[i]).setActivationFunction(function);
        }
    }
}
