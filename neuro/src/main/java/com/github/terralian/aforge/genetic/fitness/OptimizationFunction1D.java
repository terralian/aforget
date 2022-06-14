// AForge Genetic Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2007-2011
// contacts@aforgenet.com
//
package com.github.terralian.aforge.genetic.fitness;

import com.github.terralian.aforge.core.Range;
import com.github.terralian.aforge.genetic.chromosomes.BinaryChromosome;
import com.github.terralian.aforge.genetic.chromosomes.IChromosome;

/**
 * Base class for one dimensional function optimizations.
 * <p>
 * The class is aimed to be used for one dimensional function optimization problems. It implements all methods of {@link IFitnessFunction}
 * interface and requires overriding only one method - {@link OptimizationFunction}, which represents the function to optimize.
 * <p>
 * The optimization function should be greater than 0 on the specified optimization range.
 * <p>
 * The class works only with binary chromosomes ({@link BinaryChromosome}).
 * <p>
 * Sample usage:
 * 
 * <pre>
 *   // define optimization function
     public class UserFunction : OptimizationFunction1D
     {
            public UserFunction( ) :
             base( new Range( 0, 255 ) ) { }
    
        public override double OptimizationFunction( double x )
            {
                return Math.Cos( x / 23 ) * Math.Sin( x / 50 ) + 2;
            }
     }
     ...
     // create genetic population
     Population population = new Population( 40,
            new BinaryChromosome( 32 ),
            new UserFunction( ),
            new EliteSelection( ) );
        
     while ( true )
     {
            // run one epoch of the population
         population.RunEpoch( );
         // ...
     }
 * </pre>
 */
public abstract class OptimizationFunction1D implements IFitnessFunction {

    /**
     * Optimization modes.
     * <p>
     * The enumeration defines optimization modes for
     * the one dimensional function optimization.
     */
    public enum Modes {
        /**
         * Search for function's maximum value.
         */
        Maximization,
        /**
         * Search for function's minimum value.
         */
        Minimization
    }

    // optimization range
    private Range range = new Range(0, 1);
    // optimization mode
    private Modes mode = Modes.Maximization;

    @Override
    public double evaluate(IChromosome chromosome) {
        double functionValue = optimizationFunction(translate(chromosome));
        // fitness value
        return (mode == Modes.Maximization) ? functionValue : 1 / functionValue;
    }

    /**
     * Translates genotype to phenotype.
     * <p>
     * The method returns double value, which represents function's
     * input point encoded by the specified chromosome.
     * 
     * @param chromosome Chromosome, which genoteype should be translated to phenotype.
     * @return Returns chromosome's fenotype - the actual solution encoded by the chromosome.
     */
    public double translate(IChromosome chromosome) {
        // get chromosome's value and max value
        double val = ((BinaryChromosome) chromosome).getValue();
        double max = ((BinaryChromosome) chromosome).matValue();

        // translate to optimization's funtion space
        return val * range.length() / max + range.getMin();
    }

    /**
     * Function to optimize.
     * <p>
     * The method should be overloaded by inherited class to
     * specify the optimization function.
     * 
     * @param x Function's input value.
     * @return Returns function output value.
     */
    public abstract double optimizationFunction(double x);

    /**
     * Optimization range.
     */
    public Range getRange() {
        return range;
    }

    /**
     * Optimization range.
     */
    public void setRange(Range range) {
        this.range = range;
    }

    /**
     * Initializes a new instance of the {@link OptimizationFunction1D} class.
     */
    public Modes getMode() {
        return mode;
    }

    /**
     * Initializes a new instance of the {@link OptimizationFunction1D} class.
     * 
     * @return Specifies range for optimization.
     */
    public void setMode(Modes mode) {
        this.mode = mode;
    }
}
