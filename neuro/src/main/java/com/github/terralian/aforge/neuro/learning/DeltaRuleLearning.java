// AForge Neural Net Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2007-2012
// contacts@aforgenet.com
//
package com.github.terralian.aforge.neuro.learning;

import com.github.terralian.aforge.neuro.activation.IActivationFunction;
import com.github.terralian.aforge.neuro.activation.SigmoidFunction;
import com.github.terralian.aforge.neuro.layers.Layer;
import com.github.terralian.aforge.neuro.networks.ActivationNetwork;
import com.github.terralian.aforge.neuro.neurons.ActivationNeuron;

/**
 * Delta rule learning algorithm.
 * <p>
 * This learning algorithm is used to train one layer neural network of {@link ActivationNeuron} with continuous activation function, see
 * {@link SigmoidFunction} for example.
 * <p>
 * See information about <a href="http://en.wikipedia.org/wiki/Delta_rule">delta rule</a> learning algorithm.
 * 
 */
public class DeltaRuleLearning implements ISupervisedLearning {

    // network to teach
    private ActivationNetwork network;
    // learning rate
    private double learningRate = 0.1;

    /**
     * Initializes a new instance of the {@link DeltaRuleLearning} class.
     * 
     * @param network Network to teach.
     * @throws IllegalArgumentException Invalid nuaral network. It should have one layer only.
     */
    public DeltaRuleLearning(ActivationNetwork network) {
        // check layers count
        if (network.getLayers().length != 1) {
            throw new IllegalArgumentException("Invalid nuaral network. It should have one layer only.");
        }

        this.network = network;
    }

    /**
     * Runs learning iteration.
     * <p>
     * Runs one learning iteration and updates neuron's weights.
     * 
     * @return Returns squared error (difference between current network's output and desired output) divided by 2.
     */
    @Override
    public double run(double[] input, double[] output) {
        // compute output of network
        double[] networkOutput = network.compute(input);

        // get the only layer of the network
        Layer layer = network.getLayers()[0];
        // get activation function of the layer
        IActivationFunction activationFunction = ((ActivationNeuron) layer.getNeurons()[0]).getActivationFunction();

        // summary network absolute error
        double error = 0.0;

        // update weights of each neuron
        for (int j = 0; j < layer.getNeurons().length; j++) {
            // get neuron of the layer
            ActivationNeuron neuron = (ActivationNeuron) layer.getNeurons()[j];
            // calculate neuron's error
            double e = output[j] - networkOutput[j];
            // get activation function's derivative
            double functionDerivative = activationFunction.derivative2(networkOutput[j]);

            // update weights
            for (int i = 0; i < neuron.getWeights().length; i++) {
                neuron.getWeights()[i] += learningRate * e * functionDerivative * input[i];
            }

            // update threshold value
            neuron.setThreshold(neuron.getThreshold() + learningRate * e * functionDerivative);

            // sum error
            error += (e * e);
            }

        return error / 2;
    }

    /**
     * Runs learning epoch.
     * <p>
     * the method runs one learning epoch, by calling {@link #run} method
     * for each vector provided in the input array.
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
     * learningRate Default value equals to <b>0.1</b>.
     */
    public double getLearningRate() {
        return learningRate;
    }

    /**
     * Learning rate, [0, 1].
     * <p>
     * The value determines speed of learning.
     * <p>
     * learningRate Default value equals to <b>0.1</b>.
     */
    public void setLearningRate(double learningRate) {
        this.learningRate = Math.max(0.0, Math.min(1.0, learningRate));
    }

}
