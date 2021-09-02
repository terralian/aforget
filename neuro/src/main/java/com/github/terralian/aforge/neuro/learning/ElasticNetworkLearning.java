// AForge Neural Net Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2007-2012
// contacts@aforgenet.com
//
package com.github.terralian.aforge.neuro.learning;

import com.github.terralian.aforge.neuro.layers.Layer;
import com.github.terralian.aforge.neuro.networks.DistanceNetwork;
import com.github.terralian.aforge.neuro.neurons.Neuron;

/**
 * Elastic network learning algorithm.
 * <p>
 * This class implements elastic network's learning algorithm and allows to train {@link DistanceNetwork}
 */
public class ElasticNetworkLearning implements IUnsupervisedLearning {

    // neural network to train
    private DistanceNetwork network;

    // array of distances between neurons
    private double[] distance;

    // learning rate
    private double learningRate = 0.1;
    // learning radius
    private double learningRadius = 0.5;

    // squared learning radius multiplied by 2 (precalculated value to speed up computations)
    private double squaredRadius2 = 2 * 0.5 * 0.5;

    /**
     * Initializes a new instance of the {@link ElasticNetworkLearning} class.
     * 
     * @param network Neural network to train.
     */
    public ElasticNetworkLearning(DistanceNetwork network) {
        this.network = network;

        // precalculate distances array
        int neurons = network.getLayers()[0].getNeurons().length;
        double deltaAlpha = Math.PI * 2.0 / neurons;
        double alpha = deltaAlpha;

        distance = new double[neurons];
        distance[0] = 0.0;

        // calculate all distance values
        for (int i = 1; i < neurons; i++) {
            double dx = 0.5 * Math.cos(alpha) - 0.5;
            double dy = 0.5 * Math.sin(alpha);

            distance[i] = dx * dx + dy * dy;

            alpha += deltaAlpha;
        }
    }

    /**
     * Runs learning iteration.
     * <p>
     * The method runs one learning iterations - finds winner neuron (the neuron which has weights with values closest to the specified
     * input vector) and updates its weight (as well as weights of neighbor neurons) in the way to decrease difference with the specified
     * input vector.
     * 
     * @return Returns learning error - summary absolute difference between neurons' weights and appropriate inputs. The difference is
     *         measured according to the neurons distance to the winner neuron.
     */
    @Override
    public double run(double[] input) {
        double error = 0.0;

        // compute the network
        network.compute(input);
        int winner = network.getWinner();

        // get layer of the network
        Layer layer = network.getLayers()[0];

        // walk through all neurons of the layer
        for (int j = 0; j < layer.getNeurons().length; j++) {
            Neuron neuron = layer.getNeurons()[j];

            // update factor
            double factor = Math.exp(-distance[Math.abs(j - winner)] / squaredRadius2);

            // update weights of the neuron
            for (int i = 0; i < neuron.getWeights().length; i++) {
                // calculate the error
                double e = (input[i] - neuron.getWeights()[i]) * factor;
                error += Math.abs(e);
                // update weight
                neuron.getWeights()[i] += e * learningRate;
            }
        }
        return error;
    }

    /**
     * Runs learning epoch.
     * <p>
     * The method runs one learning epoch, by calling {@link #run(double[])} method for each vector provided in the {@link #input} array.
     * 
     * @return Returns summary learning error for the epoch. See {@link #run(double[])} method for details about learning error calculation.
     */
    @Override
    public double runEpoch(double[][] input) {
        double error = 0.0;

        // walk through all training samples
        for (double[] sample : input) {
            error += run(sample);
        }

        // return summary error
        return error;
    }

    /**
     * Learning rate, [0, 1].
     * <p>
     * Determines speed of learning.
     * <p>
     * Default value equals to <b>0.1</b>.
     */
    public double getLearningRate() {
        return learningRate;
    }

    /**
     * Learning rate, [0, 1].
     * <p>
     * Determines speed of learning.
     * <p>
     * Default value equals to <b>0.1</b>.
     */
    public void setLearningRate(double learningRate) {
        this.learningRadius = Math.max(0, Math.min(1.0, learningRate));
        this.squaredRadius2 = 2 * learningRadius * learningRadius;
    }

}
