// AForge Neural Net Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright © César Souza, 2009-2012
// cesarsouza at gmail.com
//
// Copyright © AForge.NET, 2005-2012
// contacts@aforgenet.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
package com.github.terralian.aforge.neuro.learning;

import com.github.terralian.aforge.neuro.activation.IActivationFunction;
import com.github.terralian.aforge.neuro.layers.ActivationLayer;
import com.github.terralian.aforge.neuro.layers.Layer;
import com.github.terralian.aforge.neuro.networks.ActivationNetwork;
import com.github.terralian.aforge.neuro.neurons.ActivationNeuron;
import com.github.terralian.csharp.LangUtil;

/**
 * Resilient Backpropagation learning algorithm.
 * <p>
 * This class implements the resilient backpropagation (RProp)
 * learning algorithm. The RProp learning algorithm is one of the fastest learning
 * algorithms for feed-forward learning networks which use only first-order
 * information.
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
 * ResilientBackpropagationLearning teacher = new ResilientBackpropagationLearning( network );
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
public class ResilientBackpropagationLearning implements ISupervisedLearning {
    private ActivationNetwork network;

    private double learningRate = 0.0125;
    private double deltaMax = 50.0;
    private double deltaMin = 1e-6;

    private final double etaMinus = 0.5;
    private double etaPlus = 1.2;

    private double[][] neuronErrors = null;

    // update values, also known as deltas
    private double[][][] weightsUpdates = null;
    private double[][] thresholdsUpdates = null;

    // current and previous gradient values
    private double[][][] weightsDerivatives = null;
    private double[][] thresholdsDerivatives = null;

    private double[][][] weightsPreviousDerivatives = null;
    private double[][] thresholdsPreviousDerivatives = null;

    /**
     * Initializes a new instance of the {@link ResilientBackpropagationLearning} class.
     * 
     * @param network Network to teach.
     */
    public ResilientBackpropagationLearning(ActivationNetwork network) {
        this.network = network;

        int layersCount = network.getLayers().length;

        neuronErrors = new double[layersCount][];

        weightsDerivatives = new double[layersCount][][];
        thresholdsDerivatives = new double[layersCount][];

        weightsPreviousDerivatives = new double[layersCount][][];
        thresholdsPreviousDerivatives = new double[layersCount][];

        weightsUpdates = new double[layersCount][][];
        thresholdsUpdates = new double[layersCount][];

        // initialize errors, derivatives and steps
        for (int i = 0; i < network.getLayers().length; i++) {
            Layer layer = network.getLayers()[i];
            int neuronsCount = layer.getNeurons().length;

            neuronErrors[i] = new double[neuronsCount];

            weightsDerivatives[i] = new double[neuronsCount][];
            weightsPreviousDerivatives[i] = new double[neuronsCount][];
            weightsUpdates[i] = new double[neuronsCount][];

            thresholdsDerivatives[i] = new double[neuronsCount];
            thresholdsPreviousDerivatives[i] = new double[neuronsCount];
            thresholdsUpdates[i] = new double[neuronsCount];

            // for each neuron
            for (int j = 0; j < layer.getNeurons().length; j++) {
                weightsDerivatives[i][j] = new double[layer.getInputsCount()];
                weightsPreviousDerivatives[i][j] = new double[layer.getInputsCount()];
                weightsUpdates[i][j] = new double[layer.getInputsCount()];
            }
        }

        // intialize steps
        resetUpdates(learningRate);
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
        // zero gradient
        resetGradient();

        // compute the network's output
        network.compute(input);

        // calculate network error
        double error = calculateError(output);

        // calculate weights updates
        calculateGradient(input);

        // update the network
        updateNetwork();

        // return summary error
        return error;
    }

    /**
     * Runs learning epoch.
     * <p>
     * The method runs one learning epoch, by calling {@link #run} method
     * method for details about learning error calculation.
     * 
     * @return Returns summary learning error for the epoch. See {@link #run}
     * method for details about learning error calculation.
     */
    @Override
    public double runEpoch(double[][] input, double[][] output) {
        // zero gradient
        resetGradient();

        double error = 0.0;

        // run learning procedure for all samples
        for (int i = 0; i < input.length; i++) {
            // compute the network's output
            network.compute(input[i]);

            // calculate network error
            error += calculateError(output[i]);

            // calculate weights updates
            calculateGradient(input[i]);
        }

        // update the network
        updateNetwork();

        // return summary error
        return error;
    }

    /**
     * Resets current weight and threshold derivatives.
     */
    private void resetGradient() {
        for (int i = 0; i < weightsDerivatives.length; i++) {
            for (int j = 0; j < weightsDerivatives[i].length; j++) {
                LangUtil.arrayClear(weightsDerivatives[i][j], 0, weightsDerivatives[i][j].length);
            }
        }

        for (int i = 0; i < thresholdsDerivatives.length; i++) {
            LangUtil.arrayClear(thresholdsDerivatives[i], 0, thresholdsDerivatives[i].length);
        }
    }

    /**
     * Resets the current update steps using the given learning rate.
     */
    private void resetUpdates(double rate) {
        for (int i = 0; i < weightsUpdates.length; i++) {
            for (int j = 0; j < weightsUpdates[i].length; j++) {
                for (int k = 0; k < weightsUpdates[i][j].length; k++) {
                    weightsUpdates[i][j][k] = rate;
                }
            }
        }

        for (int i = 0; i < thresholdsUpdates.length; i++) {
            for (int j = 0; j < thresholdsUpdates[i].length; j++) {
                thresholdsUpdates[i][j] = rate;
            }
        }
    }

    /**
     * Update network's weights.
     */
    private void updateNetwork() {
        double[][] layerWeightsUpdates;
        double[] layerThresholdUpdates;
        double[] neuronWeightUpdates;

        double[][] layerWeightsDerivatives;
        double[] layerThresholdDerivatives;
        double[] neuronWeightDerivatives;

        double[][] layerPreviousWeightsDerivatives;
        double[] layerPreviousThresholdDerivatives;
        double[] neuronPreviousWeightDerivatives;

        // for each layer of the network
        for (int i = 0; i < network.getLayers().length; i++) {
            ActivationLayer layer = (ActivationLayer) network.getLayers()[i];

            layerWeightsUpdates = weightsUpdates[i];
            layerThresholdUpdates = thresholdsUpdates[i];

            layerWeightsDerivatives = weightsDerivatives[i];
            layerThresholdDerivatives = thresholdsDerivatives[i];

            layerPreviousWeightsDerivatives = weightsPreviousDerivatives[i];
            layerPreviousThresholdDerivatives = thresholdsPreviousDerivatives[i];

            // for each neuron of the layer
            for (int j = 0; j < layer.getNeurons().length; j++) {
                ActivationNeuron neuron = (ActivationNeuron) layer.getNeurons()[j];

                neuronWeightUpdates = layerWeightsUpdates[j];
                neuronWeightDerivatives = layerWeightsDerivatives[j];
                neuronPreviousWeightDerivatives = layerPreviousWeightsDerivatives[j];

                double S = 0;

                // for each weight of the neuron
                for (int k = 0; k < neuron.getInputsCount(); k++) {
                    S = neuronPreviousWeightDerivatives[k] * neuronWeightDerivatives[k];

                    if (S > 0) {
                        neuronWeightUpdates[k] = Math.min(neuronWeightUpdates[k] * etaPlus, deltaMax);
                        neuron.getWeights()[k] -= Math.signum(neuronWeightDerivatives[k]) * neuronWeightUpdates[k];
                        neuronPreviousWeightDerivatives[k] = neuronWeightDerivatives[k];
                    } else if (S < 0) {
                        neuronWeightUpdates[k] = Math.max(neuronWeightUpdates[k] * etaMinus, deltaMin);
                        neuronPreviousWeightDerivatives[k] = 0;
                    } else {
                        neuron.getWeights()[k] -= Math.signum(neuronWeightDerivatives[k]) * neuronWeightUpdates[k];
                        neuronPreviousWeightDerivatives[k] = neuronWeightDerivatives[k];
                    }
                }

                // update treshold
                S = layerPreviousThresholdDerivatives[j] * layerThresholdDerivatives[j];

                if (S > 0) {
                    layerThresholdUpdates[j] = Math.min(layerThresholdUpdates[j] * etaPlus, deltaMax);
                    neuron.setThreshold(neuron.getThreshold() - Math.signum(layerThresholdDerivatives[j]) * layerThresholdUpdates[j]);
                    layerPreviousThresholdDerivatives[j] = layerThresholdDerivatives[j];
                } else if (S < 0) {
                    layerThresholdUpdates[j] = Math.max(layerThresholdUpdates[j] * etaMinus, deltaMin);
                    layerThresholdDerivatives[j] = 0;
                } else {
                    neuron.setThreshold(neuron.getThreshold() - Math.signum(layerThresholdDerivatives[j]) * layerThresholdUpdates[j]);
                    layerPreviousThresholdDerivatives[j] = layerThresholdDerivatives[j];
                }
            }
        }
    }

    /**
     * Calculates error values for all neurons of the network.
     * 
     * @param desiredOutput Desired output vector.
     * @return Returns summary squared error of the last layer divided by 2.
     */
    private double calculateError(double[] desiredOutput) {
        double error = 0;
        int layersCount = network.getLayers().length;

        // assume, that all neurons of the network have the same activation function
        IActivationFunction function = ((ActivationNeuron) network.getLayers()[0].getNeurons()[0]).getActivationFunction();

        // calculate error values for the last layer first
        ActivationLayer layer = (ActivationLayer) network.getLayers()[layersCount - 1];
        double[] layerDerivatives = neuronErrors[layersCount - 1];

        for (int i = 0; i < layer.getNeurons().length; i++) {
            double output = layer.getNeurons()[i].getOutput();

            double e = output - desiredOutput[i];
            layerDerivatives[i] = e * function.derivative2(output);
            error += (e * e);
        }

        // calculate error values for other layers
        for (int j = layersCount - 2; j >= 0; j--) {
            layer = (ActivationLayer) network.getLayers()[j];
            layerDerivatives = neuronErrors[j];

            ActivationLayer layerNext = (ActivationLayer) network.getLayers()[j + 1];
            double[] nextDerivatives = neuronErrors[j + 1];

            // for all neurons of the layer
            for (int i = 0, n = layer.getNeurons().length; i < n; i++) {
                double sum = 0.0;

                for (int k = 0; k < layerNext.getNeurons().length; k++) {
                    sum += nextDerivatives[k] * layerNext.getNeurons()[k].getWeights()[i];
                }

                layerDerivatives[i] = sum * function.derivative2(layer.getNeurons()[i].getOutput());
            }
        }

        // return squared error of the last layer divided by 2
        return error / 2.0;
    }

    /**
     * Calculate weights updates
     * 
     * @param input Network's input vector.
     */
    private void calculateGradient(double[] input) {
        // 1. calculate updates for the first layer
        ActivationLayer layer = (ActivationLayer) network.getLayers()[0];
        double[] weightErrors = neuronErrors[0];
        double[][] layerWeightsDerivatives = weightsDerivatives[0];
        double[] layerThresholdDerivatives = thresholdsDerivatives[0];

        // So, for each neuron of the first layer:
        for (int i = 0; i < layer.getNeurons().length; i++) {
            ActivationNeuron neuron = (ActivationNeuron) layer.getNeurons()[i];
            double[] neuronWeightDerivatives = layerWeightsDerivatives[i];

            // for each weight of the neuron:
            for (int j = 0; j < neuron.getInputsCount(); j++) {
                neuronWeightDerivatives[j] += weightErrors[i] * input[j];
            }
            layerThresholdDerivatives[i] += weightErrors[i];
        }

        // 2. for all other layers
        for (int k = 1; k < network.getLayers().length; k++) {
            layer = (ActivationLayer) network.getLayers()[k];
            weightErrors = neuronErrors[k];
            layerWeightsDerivatives = weightsDerivatives[k];
            layerThresholdDerivatives = thresholdsDerivatives[k];

            ActivationLayer layerPrev = (ActivationLayer) network.getLayers()[k - 1];

            // for each neuron of the layer
            for (int i = 0; i < layer.getNeurons().length; i++) {
                ActivationNeuron neuron = (ActivationNeuron) layer.getNeurons()[i];
                double[] neuronWeightDerivatives = layerWeightsDerivatives[i];

                // for each weight of the neuron
                for (int j = 0; j < layerPrev.getNeurons().length; j++) {
                    neuronWeightDerivatives[j] += weightErrors[i] * layerPrev.getNeurons()[j].getOutput();
                }
                layerThresholdDerivatives[i] += weightErrors[i];
            }
        }
    }

    /**
     * Learning rate.
     * <p>
     * The value determines speed of learning.
     * <p>
     * Default value equals to <b>0.0125</b>.
     */
    public double getLearningRate() {
        return learningRate;
    }

    /**
     * Learning rate.
     * <p>
     * The value determines speed of learning.
     * <p>
     * Default value equals to <b>0.0125</b>.
     */
    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
        resetUpdates(learningRate);
    }
}
