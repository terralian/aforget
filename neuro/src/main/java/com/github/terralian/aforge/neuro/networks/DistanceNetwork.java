// AForge Neural Net Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2007-2012
// contacts@aforgenet.com
//
package com.github.terralian.aforge.neuro.networks;

import com.github.terralian.aforge.neuro.layers.DistanceLayer;
import com.github.terralian.aforge.neuro.neurons.Neuron;

/**
 * Distance network.
 * <p>
 * Distance network is a neural network of only one {@link DistanceLayer}
 * The network is a base for such neural networks as SOM, Elastic net, etc.
 */
public class DistanceNetwork extends Network {

    private static final long serialVersionUID = 1L;

    /**
     * Initializes a new instance of the {@link DistanceNetwork} class.
     * <p>
     * The new network is randomized (see {@link Neuron#randomize()} method) after it is created.
     * 
     * @param inputsCount Network's inputs count.
     * @param neuronsCount Network's neurons count.
     */
    public DistanceNetwork(int inputsCount, int neuronsCount) {
        super(inputsCount, 1);
        layers[0] = new DistanceLayer(neuronsCount, inputsCount);
    }

    /**
     * Get winner neuron.
     * <p>
     * The method returns index of the neuron, which weights have
     * the minimum distance from network's input.
     * 
     * @return Index of the winner neuron.
     */
    public int getWinner() {
        // find the MIN value
        double min = output[0];
        int minIndex = 0;

        for (int i = 1; i < output.length; i++) {
            if (output[i] < min) {
                // found new MIN value
                min = output[i];
                minIndex = i;
            }
        }

        return minIndex;
    }
}
