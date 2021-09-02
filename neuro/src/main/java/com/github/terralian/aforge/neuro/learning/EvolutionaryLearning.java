// AForge Neural Net Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright © AForge.NET, 2007-2012
// contacts@aforgenet.com
//
package com.github.terralian.aforge.neuro.learning;

import com.github.terralian.aforge.core.Range;
import com.github.terralian.aforge.genetic.Population;
import com.github.terralian.aforge.genetic.chromosomes.DoubleArrayChromosome;
import com.github.terralian.aforge.genetic.selection.EliteSelection;
import com.github.terralian.aforge.genetic.selection.ISelectionMethod;
import com.github.terralian.aforge.math.random.ExponentialGenerator;
import com.github.terralian.aforge.math.random.IRandomNumberGenerator;
import com.github.terralian.aforge.math.random.UniformGenerator;
import com.github.terralian.aforge.neuro.layers.Layer;
import com.github.terralian.aforge.neuro.networks.ActivationNetwork;
import com.github.terralian.aforge.neuro.neurons.ActivationNeuron;

/**
 * Neural networks' evolutionary learning algorithm, which is based on Genetic Algorithms.
 * 
 * <p>The class implements supervised neural network's learning algorithm,
 * which is based on Genetic Algorithms. For the given neural network, it create a population
 * of <see cref="DoubleArrayChromosome"/> chromosomes, which represent neural network's
 * weights. Then, during the learning process, the genetic population evolves and weights, which
 * are represented by the best chromosome, are set to the source neural network.
 * 
 * <p>See <see cref="Population"/> class for additional information about genetic population
 * and evolutionary based search.
 * 
 * <p>
 * Sample usage (training network to calculate XOR function):
 * 
 * <pre>
 * // initialize input and output values
 * double[][] input = new double[4][] {
 *     new double[] {-1,  1}, new double[] {-1, 1},
 *     new double[] { 1, -1}, new double[] { 1, 1}
 * };
 * double[][] output = new double[4][] {
 *     new double[] {-1}, new double[] { 1},
 *     new double[] { 1}, new double[] {-1}
 * };
 * // create neural network
 * ActivationNetwork   network = new ActivationNetwork(
 *     BipolarSigmoidFunction( 2 ),
 *     2, // two inputs in the network
 *     2, // two neurons in the first layer
 *     1 ); // one neuron in the second layer
 * // create teacher
 * EvolutionaryLearning teacher = new EvolutionaryLearning( network,
 *     100 ); // number of chromosomes in genetic population
 * // loop
 * while ( !needToStop )
 * {
 *     // run epoch of learning procedure
 *     double error = teacher.RunEpoch( input, output );
 *     // check error value to see if we need to stop
 *     // ...
 * }
 * 
 * </pre>
 * 
 * @see BackPropagationLearning
 */
public class EvolutionaryLearning implements ISupervisedLearning {

    // designed network for training which have to matach inputs and outputs
    private ActivationNetwork network;
    // number of weight in the network to train
    private int numberOfNetworksWeights;

    // genetic population
    private Population population;
    // size of population
    private int populationSize;

    // generator for newly generated neurons
    private IRandomNumberGenerator chromosomeGenerator;
    // mutation generators
    private IRandomNumberGenerator mutationMultiplierGenerator;
    private IRandomNumberGenerator mutationAdditionGenerator;

    // selection method for chromosomes in population
    private ISelectionMethod selectionMethod;

    // crossover probability in genetic population
    private double crossOverRate;
    // mutation probability in genetic population
    private double mutationRate;
    // probability to add newly generated chromosome to population
    private double randomSelectionRate;
    
    /**
     * Initializes a new instance of the {@link EvolutionaryLearning} class.
     * 
     * @param activationNetwork Activation network to be trained.
     * @param populationSize Size of genetic population.
     * @param chromosomeGenerator Random numbers generator used for initialization of genetic population representing neural network's
     *        weights and thresholds (see {@link DoubleArrayChromosome#chromosomeGenerator}).
     * @param mutationMultiplierGenerator Random numbers generator used to generate random factors for multiplication of network's weights
     *        and thresholds during genetic mutation
     * @param mutationAdditionGenerator Random numbers generator used to generate random values added to neural network's weights and
     *        thresholds during genetic mutation (see {@link DoubleArrayChromosome#mutationAdditionGenerator})
     * @param selectionMethod Method of selection best chromosomes in genetic population.
     * @param crossOverRate Crossover rate in genetic population (see {@link Population#crossoverRate})
     * @param mutationRate Mutation rate in genetic population (see {@link Population#mutationRate})
     * @param randomSelectionRate Rate of injection of random chromosomes during selection in genetic population (see
     *        {@link Population#randomSelectionPortion})
     */
    public EvolutionaryLearning( ActivationNetwork activationNetwork, int populationSize,
            IRandomNumberGenerator chromosomeGenerator,
            IRandomNumberGenerator mutationMultiplierGenerator,
            IRandomNumberGenerator mutationAdditionGenerator,
            ISelectionMethod selectionMethod,
            double crossOverRate, double mutationRate, double randomSelectionRate) {
        // Check of assumptions during debugging only
        // Debug.Assert( activationNetwork != null );
        // Debug.Assert( populationSize > 0 );
        // Debug.Assert( chromosomeGenerator != null );
        // Debug.Assert( mutationMultiplierGenerator != null );
        // Debug.Assert( mutationAdditionGenerator != null );
        // Debug.Assert( selectionMethod != null );
        // Debug.Assert( crossOverRate >= 0.0 && crossOverRate <= 1.0 );
        // Debug.Assert( mutationRate >= 0.0 && crossOverRate <= 1.0 );
        // Debug.Assert( randomSelectionRate >= 0.0 && randomSelectionRate <= 1.0 );

        // networks's parameters
        this.network = activationNetwork;
        this.numberOfNetworksWeights = calculateNetworkSize(activationNetwork);

        // population parameters
        this.populationSize = populationSize;
        this.chromosomeGenerator = chromosomeGenerator;
        this.mutationMultiplierGenerator = mutationMultiplierGenerator;
        this.mutationAdditionGenerator = mutationAdditionGenerator;
        this.selectionMethod = selectionMethod;
        this.crossOverRate = crossOverRate;
        this.mutationRate = mutationRate;
        this.randomSelectionRate = randomSelectionRate;
    }

    /**
     * Initializes a new instance of the {@link EvolutionaryLearning} class.
     * <p>
     * This version of constructor is used to create genetic population for searching optimal neural network's weight using default set of
     * parameters, which are:
     * <ul>
     * <li>Selection method - elite;</li>
     * <li>Crossover rate - 0.75;</li>
     * <li>Mutation rate - 0.25;</li>
     * <li>Rate of injection of random chromosomes during selection - 0.20;</li>
     * <li>Random numbers generator for initializing new chromosome - UniformGenerator( new Range( -1, 1 ) );</li>
     * <li>Random numbers generator used during mutation for genes' multiplication - ExponentialGenerator( 1 );</li>
     * <li>Random numbers generator used during mutation for adding random value to genes - UniformGenerator( new Range( -0.5f, 0.5f )
     * ).</li>
     * </ul>
     * <p>
     * In order to have full control over the above default parameters, it is possible to used extended version of constructor, which allows
     * to specify all of the parameters.
     * 
     * @param activationNetwork Activation network to be trained.
     * @param populationSize Size of genetic population.
     */
    public EvolutionaryLearning(ActivationNetwork activationNetwork, int populationSize) {
        // Check of assumptions during debugging only
        // Debug.Assert(activationNetwork != null);
        // Debug.Assert(populationSize > 0);

        // networks's parameters
        this.network = activationNetwork;
        this.numberOfNetworksWeights = calculateNetworkSize(activationNetwork);

        // population parameters
        this.populationSize = populationSize;
        this.chromosomeGenerator = new UniformGenerator(new Range(-1, 1));
        this.mutationMultiplierGenerator = new ExponentialGenerator(1);
        this.mutationAdditionGenerator = new UniformGenerator(new Range(-0.5f, 0.5f));
        this.selectionMethod = new EliteSelection();
        this.crossOverRate = 0.75;
        this.mutationRate = 0.25;
        this.randomSelectionRate = 0.2;
    }

    // Create and initialize genetic population
    private int calculateNetworkSize(ActivationNetwork activationNetwork) {
        // caclculate total amount of weight in neural network
        int networkSize = 0;

        for (int i = 0; i < network.getLayers().length; i++) {
            Layer layer = network.getLayers()[i];

            for (int j = 0; j < layer.getNeurons().length; j++) {
                // sum all weights and threshold
                networkSize += layer.getNeurons()[j].getWeights().length + 1;
            }
        }

        return networkSize;
    }

    /**
     * Runs learning iteration.
     * <p>
     * The method is not implemented, since evolutionary learning algorithm is global and requires all inputs/outputs in order to run its
     * one epoch. Use {@link #runEpoch} method instead.
     * 
     * @throws UnsupportedOperationException The method is not implemented by design.
     */
    @Override
    public double run(double[] input, double[] output) {
        throw new UnsupportedOperationException("The method is not implemented by design.");
    }

    /**
     * Runs learning epoch.
     * <p>
     * While running the neural network's learning process, it is required to
     * pass the same input and output values for each
     * epoch. On the very first run of the method it will initialize evolutionary fitness
     * function with the given input/output. So, changing input/output in middle of the learning
     * process, will break it.
     */
    @Override
    public double runEpoch(double[][] input, double[][] output) {
        // Debug.Assert(input.Length > 0);
        // Debug.Assert(output.Length > 0);
        // Debug.Assert(input.Length == output.Length);
        // Debug.Assert(network.InputsCount == input.Length);

        // check if it is a first run and create population if so
        if ( population == null ) {
            // sample chromosome
            DoubleArrayChromosome chromosomeExample = new DoubleArrayChromosome(
                chromosomeGenerator, mutationMultiplierGenerator, mutationAdditionGenerator,
                numberOfNetworksWeights );

            // create population ...
            population = new Population( populationSize, chromosomeExample,
                new EvolutionaryFitness( network, input, output ), selectionMethod );
            // ... and configure it
            population.setCrossoverRate(crossOverRate);
            population.setMutationRate(mutationRate);
            population.setRandomSelectionPortion(randomSelectionRate);
        }

        // run genetic epoch
        population.runEpoch();

        // get best chromosome of the population
        DoubleArrayChromosome chromosome = (DoubleArrayChromosome) population.getBestChromosome();
        double[] chromosomeGenes = chromosome.getValue();

        // put best chromosome's value into neural network's weights
        int v = 0;

        for (int i = 0; i < network.getLayers().length; i++) {
            Layer layer = network.getLayers()[i];

            for (int j = 0; j < layer.getNeurons().length; j++) {
                ActivationNeuron neuron = (ActivationNeuron) layer.getNeurons()[j];

                for (int k = 0; k < neuron.getWeights().length; k++) {
                    neuron.getWeights()[k] = chromosomeGenes[v++];
                }
                neuron.setThreshold(chromosomeGenes[v++]);
            }
        }

        // Debug.Assert( v == numberOfNetworksWeights );

        return 1.0 / chromosome.getFitness();
    }

}
