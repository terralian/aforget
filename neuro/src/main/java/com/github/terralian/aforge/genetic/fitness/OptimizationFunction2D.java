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
 *   public class UserFunction : OptimizationFunction2D
     {
            public UserFunction( ) :
                base( new Range( -4, 4 ), new Range( -4, 4 ) ) { }
    
        public override double OptimizationFunction( double x, double y )
            {
                return ( Math.Cos( y ) * x * y ) / ( 2 - Math.Sin( x ) );
            }
     }
     ...
     // create genetic population
     Population population = new Population( 40,
        new BinaryChromosome( 32 ),
        new UserFunction( ),
        new EliteSelection( ) );
     // run one epoch of the population
     population.RunEpoch( );
 * </pre>
 */
public abstract class OptimizationFunction2D implements IFitnessFunction {

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
    private Range rangeX = new Range(0, 1);
    private Range rangeY = new Range(0, 1);
    // optimization mode
    private Modes mode = Modes.Maximization;

    /**
     * Initializes a new instance of the {@link OptimizationFunction2D} class.
     * 
     * @param rangeX Specifies X variable's range.
     * @param rangeY Specifies Y variable's range.
     */
    public OptimizationFunction2D(Range rangeX, Range rangeY) {
        this.rangeX = rangeX;
        this.rangeY = rangeY;
    }

    /**
     * Evaluates chromosome.
     * <p>
     * The method calculates fitness value of the specified chromosome.
     * 
     * @param chromosome Chromosome to evaluate.
     * @return Returns chromosome's fitness value.
     */
    @Override
    public double evaluate(IChromosome chromosome) {
        double[] xy;

        // do native translation first
        xy = translate(chromosome);
        // get function value
        double functionValue = optimizationFunction(xy[0], xy[1]);
        // return fitness value
        return (mode == Modes.Maximization) ? functionValue : 1 / functionValue;
    }

    public double[] translate(IChromosome chromosome) {
        // get chromosome's value
        long val = ((BinaryChromosome) chromosome).getValue();
        // chromosome's length
        int length = ((BinaryChromosome) chromosome).getLength();
        // length of X component
        int xLength = length / 2;
        // length of Y component
        int yLength = length - xLength;
        // X maximum value - equal to X mask
        long xMax = Long.MAX_VALUE >> (64 - xLength);
        // Y maximum value
        long yMax = Long.MAX_VALUE >> (64 - yLength);
        // X component
        double xPart = val & xMax;
        // Y component;
        double yPart = val >> xLength;

        // translate to optimization's funtion space
        double[] ret = new double[2];

        ret[0] = xPart * rangeX.length() / xMax + rangeX.getMin();
        ret[1] = yPart * rangeY.length() / yMax + rangeY.getMin();

        return ret;
    }

    /**
     * Function to optimize.
     * <p>
     * The method should be overloaded by inherited class to specify the optimization function.
     * 
     * @param x Function's input value.
     * @param y Function Y input value.
     * @return Returns function output value.
     */
    public abstract double optimizationFunction(double x, double y);

    /**
     * X variable's optimization range.
     * <p>
     * Defines function's X range. The function's extreme will be searched in this range only.
     */
    public Range getRangeX() {
        return rangeX;
    }

    /**
     * X variable's optimization range.
     * <p>
     * Defines function's X range. The function's extreme will
     * be searched in this range only.
     */
    public void setRangeX(Range rangeX) {
        this.rangeX = rangeX;
    }

    /**
     * Y variable's optimization range.
     * <p>
     * Defines function's Y range. The function's extreme will
     * be searched in this range only.
     */
    public Range getRangeY() {
        return rangeY;
    }

    /**
     * Y variable's optimization range.
     * <p>
     * Defines function's Y range. The function's extreme will
     * be searched in this range only.
     */
    public void setRangeY(Range rangeY) {
        this.rangeY = rangeY;
    }

    /**
     * Optimization mode.
     * <p>
     * Defines optimization mode - what kind of extreme to search.
     */
    public Modes getMode() {
        return mode;
    }

    /**
     * Optimization mode.
     * <p>
     * Defines optimization mode - what kind of extreme to search.
     */
    public void setMode(Modes mode) {
        this.mode = mode;
    }
}
