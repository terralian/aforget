// AForge Neural Net Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2007-2012
// contacts@aforgenet.com
//
package com.github.terralian.aforge.neuro.layers;

import com.github.terralian.aforge.neuro.neurons.DistanceNeuron;
import com.github.terralian.aforge.neuro.neurons.Neuron;

/**
 * Distance layer.
 * <p>
 * Distance layer is a layer of {@link DistanceNeuron}
 * The layer is usually a single layer of such networks as Kohonen Self
 * Organizing Map, Elastic Net, Hamming Memory Net.
 */
public class DistanceLayer extends Layer {

    private static final long serialVersionUID = 1L;

    /**
     * Initializes a new instance of the {@link DistanceLayer} class.
     * <p>
     * The new layet is randomized (see {@link Neuron#randomize()} method) after it is created.
     * 
     * @param neuronsCount Layer's neurons count.
     * @param inputsCount Layer's inputs count.
     */
    public DistanceLayer(int neuronsCount, int inputsCount) {
        super(neuronsCount, inputsCount);
        for (int i = 0; i < neuronsCount; i++)
            neurons[i] = new DistanceNeuron(inputsCount);
    }

}
