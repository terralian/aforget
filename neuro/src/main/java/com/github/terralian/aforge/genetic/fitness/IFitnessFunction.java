// AForge Genetic Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � Andrew Kirillov, 2006-2009
// andrew.kirillov@aforgenet.com
//
package com.github.terralian.aforge.genetic.fitness;

import com.github.terralian.aforge.genetic.chromosomes.IChromosome;

/**
 * Fitness function interface.
 * <p>
 * The interface should be implemented by all fitness function
 * classes, which are supposed to be used for calculation of chromosomes
 * fitness values. All fitness functions should return positive (<b>greater
 * then zero</b>) value, which indicates how good is the evaluated chromosome - 
 * the greater the value, the better the chromosome.
 */
public interface IFitnessFunction {

    /**
     * Evaluates chromosome.
     * <p>
     * The method calculates fitness value of the specified chromosome.
     * 
     * @param chromosome Chromosome to evaluate.
     * @return Returns chromosome's fitness value.
     */
    double evaluate(IChromosome chromosome);
}
