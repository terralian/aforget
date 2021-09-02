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
 * Kohonen Self Organizing Map (SOM) learning algorithm.
 * <p>
 * This class implements Kohonen's SOM learning algorithm and
 * is widely used in clusterization tasks. The class allows to train
 * <see cref="DistanceNetwork">Distance Networks</see>.
 * <p>
 * Sample usage (clustering RGB colors):
 * <pre>
 * // set range for randomization neurons' weights
 * Neuron.RandRange = new Range( 0, 255 );
 * // create network
 * DistanceNetwork network = new DistanceNetwork(
 *         3, // thress inputs in the network
 *         100 * 100 ); // 10000 neurons
 * // create learning algorithm
 * SOMLearning trainer = new SOMLearning( network );
 * // network's input
 * double[] input = new double[3];
 * // loop
 * while ( !needToStop )
 * {
 *     input[0] = rand.Next( 256 );
 *     input[1] = rand.Next( 256 );
 *     input[2] = rand.Next( 256 );
 * 
 *     trainer.Run( input );
 * 
 *     // ...
 *     // update learning rate and radius continuously,
 *     // so networks may come steady state
 * </pre>
 */
public class SOMLearning implements IUnsupervisedLearning {

    // neural network to train
    private DistanceNetwork network;
    // network's dimension
    private int width;
    private int height;

    // learning rate
    private double learningRate = 0.1;
    // learning radius
    private double learningRadius = 7;

    // squared learning radius multiplied by 2 (precalculated value to speed up computations)
    private double squaredRadius2 = 2 * 7 * 7;

    /**
     * Initializes a new instance of the {@link SOMLearning} class.
     * <p>
     * This constructor supposes that a square network will be passed for training - it should be possible to get square root of network's
     * neurons amount.
     * 
     * @param network Neural network to train.
     * @throws IllegalArgumentException Invalid network size - square network is expected.
     */
    public SOMLearning(DistanceNetwork network) {
        // network's dimension was not specified, let's try to guess
        int neuronsCount = network.getLayers()[0].getNeurons().length;
        int width = (int) Math.sqrt(neuronsCount);

        if (width * width != neuronsCount) {
            throw new IllegalArgumentException("Invalid network size.");
        }

        // ok, we got it
        this.network = network;
        this.width = width;
        this.height = width;
    }

    /**
     * Initializes a new instance of the {@link SOMLearning} class.
     * <p>
     * The constructor allows to pass network of arbitrary rectangular shape.
     * The amount of neurons in the network should be equal to <b>width</b> * <b>height</b>.
     * 
     * @param network Neural network to train.
     * @param width Neural network's width.
     * @param height Neural network's height.
     */
    public SOMLearning(DistanceNetwork network, int width, int height) {
        // check network size
        if (network.getLayers()[0].getNeurons().length != width * height) {
            throw new IllegalArgumentException("Invalid network size.");
        }

        this.network = network;
        this.width = width;
        this.height = height;
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

        // check learning radius
        if (learningRadius == 0) {
            Neuron neuron = layer.getNeurons()[winner];

            // update weight of the winner only
            for (int i = 0; i < neuron.getWeights().length; i++) {
                // calculate the error
                double e = input[i] - neuron.getWeights()[i];
                error += Math.abs(e);
                // update weights
                neuron.getWeights()[i] += e * learningRate;
            }
        } else {
            // winner's X and Y
            int wx = winner % width;
            int wy = winner / width;

            // walk through all neurons of the layer
            for (int j = 0; j < layer.getNeurons().length; j++) {
                Neuron neuron = layer.getNeurons()[j];

                int dx = (j % width) - wx;
                int dy = (j / width) - wy;

                // update factor ( Gaussian based )
                double factor = Math.exp(-(double) (dx * dx + dy * dy) / squaredRadius2);

                // update weight of the neuron
                for (int i = 0; i < neuron.getWeights().length; i++) {
                    // calculate the error
                    double e = (input[i] - neuron.getWeights()[i]) * factor;
                    error += Math.abs(e);
                    // update weight
                    neuron.getWeights()[i] += e * learningRate;
                }
            }
        }
        return error;
    }

    /**
     * Runs learning epoch.
     * <p>
     * The method runs one learning epoch, by calling {@link #run}  method
     * for each vector provided in the input array.
     * 
     * @return Returns summary learning error for the epoch. See {@link #run} 
     * method for details about learning error calculation.
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
     * The value determines speed of learning.
     * <p>
     * Default value equals to <b>0.1</b>.
     */
    public double getLearningRate() {
        return learningRate;
    }

    /**
     * Learning rate, [0, 1].
     * <p>
     * The value determines speed of learning.
     * <p>
     * Default value equals to <b>0.1</b>.
     */
    public void setLearningRate(double learningRate) {
        this.learningRate = Math.max(0.0, Math.min(1.0, learningRate));
    }

    /**
     * Learning radius.
     * <p>
     * Determines the amount of neurons to be updated around
     * winner neuron. Neurons, which are in the circle of specified radius,
     * are updated during the learning procedure. Neurons, which are closer
     * to the winner neuron, get more update.
     * <p>
     * In the case if learning rate is set to 0, then only winner
     * neuron's weights are updated.
     * <p>
     * >Default value equals to <b>7</b>.
     */
    public double getLearningRadius() {
        return learningRadius;
    }

    /**
     * Learning radius.
     * <p>
     * Determines the amount of neurons to be updated around
     * winner neuron. Neurons, which are in the circle of specified radius,
     * are updated during the learning procedure. Neurons, which are closer
     * to the winner neuron, get more update.
     * <p>
     * In the case if learning rate is set to 0, then only winner
     * neuron's weights are updated.
     * <p>
     * >Default value equals to <b>7</b>.
     */
    public void setLearningRadius(double learningRadius) {
        this.learningRadius = learningRadius;
    }
}
