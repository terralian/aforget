// AForge Genetic Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � Andrew Kirillov, 2006-2009
// andrew.kirillov@aforgenet.com
//
package com.github.terralian.aforge.genetic.chromosomes;

import com.github.terralian.aforge.genetic.fitness.IFitnessFunction;

/**
 * Chromosome interface.
 * <p>
 * The interfase should be implemented by all classes, which implement particular chromosome type.
 */
public interface IChromosome extends Comparable<IChromosome> {

    /**
     * Chromosome's fitness value.
     * <p>
     * The fitness value represents chromosome's usefulness - the greater the value, the more useful it is.
     */
    double getFitness();

    /**
     * Generate random chromosome value.
     * <p>
     * Regenerates chromosome's value using random number generator.
     */
    void generate();
    
    /**
     * Create new random chromosome with same parameters (factory method).
     * <p>
     * The method creates new chromosome of the same type, but randomly
     * initialized. The method is useful as factory method for those classes, which work
     * with chromosome's interface, but not with particular chromosome class.
     */
    IChromosome createNew();

    /**
     * Clone the chromosome.
     * <p>
     * The method clones the chromosome returning the exact copy of it.
     */
    IChromosome clone();

    /**
     * Mutation operator.
     * <p>
     * The method performs chromosome's mutation, changing its part randomly.
     */
    void mutate();

    /**
     * Crossover operator.
     * <p>
     * The method performs crossover between two chromosomes � interchanging some parts of chromosomes.
     * 
     * @param pair Pair chromosome to crossover with.
     */
    void crossover(IChromosome pair);

    /**
     * Evaluate chromosome with specified fitness function.
     * <p>
     * Calculates chromosome's fitness using the specifed fitness function.
     * 
     * @param function Fitness function to use for evaluation of the chromosome.
     */
    void evaluate(IFitnessFunction function);
}
