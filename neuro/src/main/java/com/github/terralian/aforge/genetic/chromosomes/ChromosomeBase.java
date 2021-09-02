// AForge Genetic Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright © Andrew Kirillov, 2006-2009
// andrew.kirillov@aforgenet.com
//
package com.github.terralian.aforge.genetic.chromosomes;

import com.github.terralian.aforge.genetic.fitness.IFitnessFunction;

/**
 * Chromosomes' base class.
 * <p>
 * The base class provides implementation of some {@link IChromosome}
 * methods and properties, which are identical to all types of chromosomes.
 */
public abstract class ChromosomeBase implements IChromosome {

    /** Chromosome's fitness value. */
    protected double fitness = 0;

    @Override
    public void evaluate(IFitnessFunction function) {
        fitness = function.evaluate(this);
    }

    /**
     * Compare two chromosomes.
     * 
     * @param o Binary chromosome to compare to.
     * @return Returns comparison result, which equals to 0 if fitness values of both chromosomes are equal, 1 if fitness value of this
     *         chromosome is less than fitness value of the specified chromosome, -1 otherwise.
     */
    @Override
    public int compareTo(IChromosome o) {
        double f = ((ChromosomeBase) o).fitness;
        return (fitness == f) ? 0 : (fitness < f) ? 1 : -1;
    }

    @Override
    public double getFitness() {
        return fitness;
    }

    @Override
    public IChromosome clone() {
        throw new UnsupportedOperationException();
    }
}
