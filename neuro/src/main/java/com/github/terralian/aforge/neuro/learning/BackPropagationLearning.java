// AForge Neural Net Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2005-2012
// contacts@aforgenet.com
//
// Copyright � Cezary Wagner, 2008
// changes optimizing algorithm performance
// Cezary.Wagner@gmail.com
//
package com.github.terralian.aforge.neuro.learning;

import com.github.terralian.aforge.neuro.activation.IActivationFunction;
import com.github.terralian.aforge.neuro.layers.Layer;
import com.github.terralian.aforge.neuro.networks.ActivationNetwork;
import com.github.terralian.aforge.neuro.neurons.ActivationNeuron;

/**
 * Back propagation learning algorithm.
 * <p>
 * The class implements back propagation learning algorithm,
 * which is widely used for training multi-layer neural networks with
 * continuous activation functions.
 * <p>
 * Sample usage (training network to calculate XOR function):
 * <pre>
 * // initialize input and output values
 * double[][] input = new double[4][] {
 *     new double[] {0, 0}, new double[] {0, 1},
 *     new double[] {1, 0}, new double[] {1, 1}
 * };
 * double[][] output = new double[4][] {
 *     new double[] {0}, new double[] {1},
 *     new double[] {1}, new double[] {0}
 * };
 * // create neural network
 * ActivationNetwork   network = new ActivationNetwork(
 *     SigmoidFunction( 2 ),
 *     2, // two inputs in the network
 *     2, // two neurons in the first layer
 *     1 ); // one neuron in the second layer
 * // create teacher
 * BackPropagationLearning teacher = new BackPropagationLearning( network );
 * // loop
 * while ( !needToStop )
 * {
 *     // run epoch of learning procedure
 *     double error = teacher.RunEpoch( input, output );
 *     // check error value to see if we need to stop
 *     // ...
 * }
 * </pre>
 */
public class BackPropagationLearning {
    // network to teach
    private ActivationNetwork network;
    // learning rate
    private double learningRate = 0.1;
    // momentum
    private double momentum = 0.0;

    // neuron's errors
    private double[][] neuronErrors = null;
    // weight's updates
    private double[][][] weightsUpdates = null;
    // threshold's updates
    private double[][] thresholdsUpdates = null;

    /**
     * Initializes a new instance of the {@link BackPropagationLearning} class.
     * 
     * @param network Network to teach.
     */
    public BackPropagationLearning(ActivationNetwork network) {
        this.network = network;

        // create error and deltas arrays
        neuronErrors = new double[network.getLayers().length][];
        weightsUpdates = new double[network.getLayers().length][][];
        thresholdsUpdates = new double[network.getLayers().length][];

        // initialize errors and deltas arrays for each layer
        for (int i = 0; i < network.getLayers().length; i++) {
            Layer layer = network.getLayers()[i];

            neuronErrors[i] = new double[layer.getNeurons().length];
            weightsUpdates[i] = new double[layer.getNeurons().length][];
            thresholdsUpdates[i] = new double[layer.getNeurons().length];

            // for each neuron
            for (int j = 0; j < weightsUpdates[i].length; j++) {
                weightsUpdates[i][j] = new double[layer.getInputsCount()];
            }
        }
    }

    /**
     * Runs learning iteration.
     * <p>
     * Runs one learning iteration and updates neuron's weights.
     * 
     * @param input Input vector.
     * @param output Desired output vector.
     * @return Returns squared error (difference between current network's output and desired output) divided by 2.
     */
    public double run(double[] input, double[] output) {
        // compute the network's output
        network.compute(input);

        // calculate network error
        double error = calculateError(output);

        // calculate weights updates
        calculateUpdates(input);

        // update the network
        updateNetwork();

        return error;
    }

    /**
     * Runs learning epoch.
     * <p>
     * The method runs one learning epoch, by calling {@link #run} method
     * for each vector provided in the {@link #input} array.
     * 
     * @param input Array of input vectors.
     * @param output Array of output vectors.
     * @return Returns summary learning error for the epoch. See {@link #run} method for details about learning error calculation.
     */
    public double runEpoch(double[][] input, double[][] output) {
        double error = 0.0;

        // run learning procedure for all samples
        for (int i = 0; i < input.length; i++) {
            error += run(input[i], output[i]);
        }

        // return summary error
        return error;
    }

    /**
     * Calculates error values for all neurons of the network.
     * 
     * @param desiredOutput Desired output vector.
     * @return Returns summary squared error of the last layer divided by 2.
     */
    private double calculateError( double[] desiredOutput ) {
        // current and the next layers
        Layer layer, layerNext;
        // current and the next errors arrays
        double[] errors, errorsNext;
        // error values
        double error = 0, e, sum;
        // neuron's output value
        double output;
        // layers count
        int layersCount = network.getLayers().length;

        // assume, that all neurons of the network have the same activation function
        IActivationFunction function = ((ActivationNeuron) network.getLayers()[0].getNeurons()[0]).getActivationFunction();

        // calculate error values for the last layer first
        layer = network.getLayers()[layersCount - 1];
        errors = neuronErrors[layersCount - 1];

        for (int i = 0; i < layer.getNeurons().length; i++) {
            output = layer.getNeurons()[i].getOutput();
            // error of the neuron
            e = desiredOutput[i] - output;
            // error multiplied with activation function's derivative
            errors[i] = e * function.derivative2(output);
            // squre the error and sum it
            error += ( e * e );
        }

        // calculate error values for other layers
        for (int j = layersCount - 2; j >= 0; j--) {
            layer = network.getLayers()[j];
            layerNext = network.getLayers()[j + 1];
            errors = neuronErrors[j];
            errorsNext = neuronErrors[j + 1];

            // for all neurons of the layer
            for (int i = 0; i < layer.getNeurons().length; i++) {
                sum = 0.0;
                // for all neurons of the next layer
                for (int k = 0; k < layerNext.getNeurons().length; k++) {
                    sum += errorsNext[k] * layerNext.getNeurons()[k].getWeights()[i];
                }
                errors[i] = sum * function.derivative2(layer.getNeurons()[i].getOutput());
            }
        }

        // return squared error of the last layer divided by 2
        return error / 2.0;
    }

    /**
     * Calculate weights updates.
     * 
     * @param input Network's input vector.
     */
    private void calculateUpdates(double[] input) {
        // current neuron
        // Neuron neuron;
        // current and previous layers
        Layer layer, layerPrev;
        // layer's weights updates
        double[][] layerWeightsUpdates;
        // layer's thresholds updates
        double[] layerThresholdUpdates;
        // layer's error
        double[] errors;
        // neuron's weights updates
        double[] neuronWeightUpdates;
        // error value
        // double error;

        // 1 - calculate updates for the first layer
        layer = network.getLayers()[0];
        errors = neuronErrors[0];
        layerWeightsUpdates = weightsUpdates[0];
        layerThresholdUpdates = thresholdsUpdates[0];

        // cache for frequently used values
        double cachedMomentum = learningRate * momentum;
        double cached1mMomentum = learningRate * (1 - momentum);
        double cachedError;

        // for each neuron of the layer
        for (int i = 0; i < layer.getNeurons().length; i++) {
            // neuron = layer.getNeurons()[i];
            cachedError = errors[i] * cached1mMomentum;
            neuronWeightUpdates = layerWeightsUpdates[i];

            // for each weight of the neuron
            for (int j = 0; j < neuronWeightUpdates.length; j++) {
                // calculate weight update
                neuronWeightUpdates[j] = cachedMomentum * neuronWeightUpdates[j] + cachedError * input[j];
            }

            // calculate treshold update
            layerThresholdUpdates[i] = cachedMomentum * layerThresholdUpdates[i] + cachedError;
        }

        // 2 - for all other layers
        for (int k = 1; k < network.getLayers().length; k++) {
            layerPrev = network.getLayers()[k - 1];
            layer = network.getLayers()[k];
            errors = neuronErrors[k];
            layerWeightsUpdates = weightsUpdates[k];
            layerThresholdUpdates = thresholdsUpdates[k];

            // for each neuron of the layer
            for (int i = 0; i < layer.getNeurons().length; i++) {
                // neuron = layer.getNeurons()[i];
                cachedError = errors[i] * cached1mMomentum;
                neuronWeightUpdates = layerWeightsUpdates[i];

                // for each synapse of the neuron
                for (int j = 0; j < neuronWeightUpdates.length; j++) {
                    // calculate weight update
                    neuronWeightUpdates[j] = cachedMomentum * neuronWeightUpdates[j] + cachedError * layerPrev.getNeurons()[j].getOutput();
                }

                // calculate treshold update
                layerThresholdUpdates[i] = cachedMomentum * layerThresholdUpdates[i] + cachedError;
            }
        }
    }

    /**
     * Update network'sweights.
     */
    private void updateNetwork() {
        // current neuron
        ActivationNeuron neuron;
        // current layer
        Layer layer;
        // layer's weights updates
        double[][] layerWeightsUpdates;
        // layer's thresholds updates
        double[] layerThresholdUpdates;
        // neuron's weights updates
        double[] neuronWeightUpdates;

        // for each layer of the network
        for (int i = 0; i < network.getLayers().length; i++) {
            layer = network.getLayers()[i];
            layerWeightsUpdates = weightsUpdates[i];
            layerThresholdUpdates = thresholdsUpdates[i];

            // for each neuron of the layer
            for (int j = 0; j < layer.getNeurons().length; j++) {
                neuron = (ActivationNeuron) layer.getNeurons()[j];
                neuronWeightUpdates = layerWeightsUpdates[j];

                // for each weight of the neuron
                for (int k = 0; k < neuron.getWeights().length; k++) {
                    // update weight
                    neuron.getWeights()[k] += neuronWeightUpdates[k];
                }
                // update treshold
                neuron.setThreshold(neuron.getThreshold() + layerThresholdUpdates[j]);
            }
        }
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
     * Momentum, [0, 1].
     * <p>
     * The value determines the portion of previous weight's update
     * to use on current iteration. Weight's update values are calculated on
     * each iteration depending on neuron's error. The momentum specifies the amount
     * of update to use from previous iteration and the amount of update
     * to use from current iteration. If the value is equal to 0.1, for example,
     * then 0.1 portion of previous update and 0.9 portion of current update are used
     * to update weight's value.
     * <p>
     * Default value equals to <b>0.0</b>.
     */
    public double getMomentum() {
        return momentum;
    }

    /**
     * Momentum, [0, 1].
     * <p>
     * The value determines the portion of previous weight's update
     * to use on current iteration. Weight's update values are calculated on
     * each iteration depending on neuron's error. The momentum specifies the amount
     * of update to use from previous iteration and the amount of update
     * to use from current iteration. If the value is equal to 0.1, for example,
     * then 0.1 portion of previous update and 0.9 portion of current update are used
     * to update weight's value.
     * <p>
     * Default value equals to <b>0.0</b>.
     */
    public void setMomentum(double momentum) {
        this.momentum = Math.max(0.0, Math.min(1.0, momentum));
    }
}
