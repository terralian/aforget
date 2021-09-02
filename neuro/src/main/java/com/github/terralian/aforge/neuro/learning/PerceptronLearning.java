// AForge Neural Net Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2007-2012
// contacts@aforgenet.com
//
package com.github.terralian.aforge.neuro.learning;

import com.github.terralian.aforge.neuro.activation.ThresholdFunction;
import com.github.terralian.aforge.neuro.layers.Layer;
import com.github.terralian.aforge.neuro.networks.ActivationNetwork;
import com.github.terralian.aforge.neuro.neurons.ActivationNeuron;

/**
 * Perceptron learning algorithm.
 * <p>
 * This learning algorithm is used to train one layer neural
 * network of {@link ActivationNeuron} with the {@link ThresholdFunction}
 * activation function.
 * <p>
 * See information about <a href="http://en.wikipedia.org/wiki/Perceptron">Perceptron</a>
 * and its learning algorithm.
 */
public class PerceptronLearning implements ISupervisedLearning {

    // network to teach
    private ActivationNetwork network;
    // learning rate
    private double learningRate = 0.1;

    /**
     * Initializes a new instance of the {@link PerceptronLearning} class.
     * 
     * @param network Network to teach.
     * @throws IllegalArgumentException Invalid nuaral network. It should have one layer only.
     */
    public PerceptronLearning(ActivationNetwork network) {
        // check layers count
        if (network.getLayers().length != 1) {
            throw new IllegalArgumentException("Invalid nuaral network. It should have one layer only.");
        }

        this.network = network;
    }

    /**
     * Runs learning iteration.
     * <p>
     * Runs one learning iteration and updates neuron's
     * weights in the case if neuron's output is not equal to the
     * desired output.
     * 
     * @return Returns absolute error - difference between current network's output and desired output.
     */
    @Override
    public double run(double[] input, double[] output) {
        // compute output of network
        double[] networkOutput = network.compute( input );

        // get the only layer of the network
        Layer layer = network.getLayers()[0];

        // summary network absolute error
        double error = 0.0;

        // check output of each neuron and update weights
        for (int j = 0; j < layer.getNeurons().length; j++) {
            double e = output[j] - networkOutput[j];

            if (e != 0) {
                ActivationNeuron perceptron = (ActivationNeuron) layer.getNeurons()[j];

                // update weights
                for (int i = 0; i < perceptron.getWeights().length; i++) {
                    perceptron.getWeights()[i] += learningRate * e * input[i];
                }

                // update threshold value
                perceptron.setThreshold(perceptron.getThreshold() + learningRate * e);

                // make error to be absolute
                error += Math.abs(e);
            }
        }

        return error;
    }

    /**
     * Runs learning epoch.
     * <p>
     * The method runs one learning epoch, by calling {@link #run} method
     * for each vector provided in the input array.
     * 
     * @return Returns summary learning error for the epoch. See {@link #run}
     * method for details about learning error calculation.
     */
    @Override
    public double runEpoch(double[][] input, double[][] output) {
        double error = 0.0;

        // run learning procedure for all samples
        for (int i = 0, n = input.length; i < n; i++) {
            error += run(input[i], output[i]);
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

}
