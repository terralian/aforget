// AForge Genetic Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � Andrew Kirillov, 2006-2009
// andrew.kirillov@aforgenet.com
//
package com.github.terralian.aforge.genetic.fitness;

import com.github.terralian.aforge.core.PolishExpression;
import com.github.terralian.aforge.genetic.chromosomes.IChromosome;

/**
 * Fitness function for symbolic regression (function approximation) problem
 * <p>
 * The fitness function calculates fitness value of {@link GPTreeChromosome} and {@link GEPChromosome} chromosomes with the aim of solving
 * symbolic regression problem. The fitness function's value is computed as:
 * 
 * <pre>
 * 100.0 / (error + 1)
 * </pre>
 * 
 * where <b>error</b> equals to the sum of absolute differences between function values (computed using the function encoded by chromosome)
 * and input values (function to be approximated).
 * <p>
 * Sample usage:
 * 
 * <pre>
    // constants
    double[] constants = new double[5] { 1, 2, 3, 5, 7 };
    // function to be approximated
    double[,] data = new double[5, 2] {
        {1, 1}, {2, 3}, {3, 6}, {4, 10}, {5, 15} };
    // create population
    Population population = new Population( 100,
        new GPTreeChromosome( new SimpleGeneFunction( 1 + constants.Length ) ),
        new SymbolicRegressionFitness( data, constants ),
        new EliteSelection( ) );
    // run one epoch of the population
    population.RunEpoch( );
 * </pre>
 */
public class SymbolicRegressionFitness implements IFitnessFunction {

    // regression data
    private double[][] data;
    // varibles
    private double[] variables;

    /**
     * Initializes a new instance of the {@link SymbolicRegressionFitness} class.
     * 
     * @param data parameter defines the function to be approximated and represents a two dimensional array of (x, y) points.
     * @param constants parameter is an array of constants, which can be used as additional variables for a genetic expression. The actual
     *        amount of variables for genetic expression equals to the amount of constants plus one - the <b>x</b> variable.
     */
    public SymbolicRegressionFitness(double[][] data, double[] constants) {
        this.data = data;
        // copy constants
        variables = new double[constants.length + 1];
        System.arraycopy(constants, 0, variables, 1, constants.length);
    }

    @Override
    public double evaluate(IChromosome chromosome) {
     // get function in polish notation
        String function = chromosome.toString( );

        // go through all the data
        double error = 0.0;
        for (int i = 0, n = data.length; i < n; i++)
        {
            // put next X value to variables list
            variables[0] = data[i][0];
            // avoid evaluation errors
            try
            {
                // evalue the function
                double y = PolishExpression.evaluate(function, variables);
                // check for correct numeric value
                if (Double.isNaN(y))
                    return 0;
                // get the difference between evaluated Y and real Y
                // and sum error
                error += Math.abs(y - data[i][1]);
            }
            catch(Exception e)
            {
                return 0;
            }
        }

        // return optimization function value
        return 100.0 / ( error + 1 );
    }

}
