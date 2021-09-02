// AForge Neural Net Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright © AForge.NET, 2007-2012
// contacts@aforgenet.com
//
// Copyright © Cezary Wagner, 2008
// Initial implementation of evolutionary learning algorithm
// Cezary.Wagner@gmail.com
//
package com.github.terralian.aforge.neuro.learning;

import com.github.terralian.aforge.genetic.chromosomes.DoubleArrayChromosome;
import com.github.terralian.aforge.genetic.chromosomes.IChromosome;
import com.github.terralian.aforge.genetic.fitness.IFitnessFunction;
import com.github.terralian.aforge.neuro.layers.Layer;
import com.github.terralian.aforge.neuro.networks.ActivationNetwork;
import com.github.terralian.aforge.neuro.neurons.ActivationNeuron;

/**
 * Fitness function used for chromosomes representing collection of neural network's weights.
 */
public class EvolutionaryFitness implements IFitnessFunction {

    // neural network for which fitness will be calculated
    private ActivationNetwork network;

    // input data samples for neural network
    private double[][] input;

    // output data samples for neural network (desired output)
    private double[][] output;

    /**
     * Initializes a new instance of the {@link EvolutionaryFitness} class.
     * 
     * @param network Neural network for which fitness will be calculated.
     * @param input Input data samples for neural network.
     * @param output Output data sampels for neural network (desired output).
     * 
     * @throws IllegalArgumentException Length of inputs and outputs arrays must be equal and greater than 0.
     * @throws IllegalArgumentException Length of each input vector must be equal to neural network's inputs count.
     */
    public EvolutionaryFitness(ActivationNetwork network, double[][] input, double[][] output) {
        if ((input.length == 0) || (input.length != output.length)) {
            throw new IllegalArgumentException("Length of inputs and outputs arrays must be equal and greater than 0.");
        }

        if (network.getInputsCount() != input[0].length) {
            throw new IllegalArgumentException("Length of each input vector must be equal to neural network's inputs count.");
        }

        this.network = network;
        this.input = input;
        this.output = output;
    }


    @Override
    public double evaluate(IChromosome chromosome) {
        DoubleArrayChromosome daChromosome = (DoubleArrayChromosome) chromosome;
        double[] chromosomeGenes = daChromosome.getValue();
        // total number of weight in neural network
        int totalNumberOfWeights = 0;

        // asign new weights and thresholds to network from the given chromosome
        for (int i = 0, layersCount = network.getLayers().length; i < layersCount; i++)
            {
            Layer layer = network.getLayers()[i];

            for (int j = 0; j < layer.getNeurons().length; j++)
                {
                ActivationNeuron neuron = (ActivationNeuron) layer.getNeurons()[j];

                for (int k = 0; k < neuron.getWeights().length; k++) {
                    neuron.getWeights()[k] = chromosomeGenes[totalNumberOfWeights++];
                }
                neuron.setThreshold(chromosomeGenes[totalNumberOfWeights++]);
                }
            }

        // post check if all values are processed and lenght of chromosome
        // is equal to network size
        // Debug.Assert( totalNumberOfWeights == daChromosome.length );

        double totalError = 0;

        for (int i = 0, inputVectorsAmount = input.length; i < inputVectorsAmount; i++)
            {
            double[] computedOutput = network.compute(input[i]);

            for (int j = 0, outputLength = output[0].length; j < outputLength; j++) {
                double error = output[i][j] - computedOutput[j];
                totalError += error * error;
            }
            }

        if (totalError > 0)
            return 1.0 / totalError;

        // zero error means the best fitness
        return Double.MAX_VALUE;
    }

}
