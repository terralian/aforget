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
 * Fitness function for times series prediction problem
 * <p>
 * The fitness function calculates fitness value of {@link GPTreeChromosome} and {@link GEPChromosome} chromosomes with the aim of solving
 * times series prediction problem using sliding window method. The fitness function's value is computed as:
 * 
 * <pre>
 * 100.0 / (error + 1)
 * </pre>
 * 
 * where <b>error</b> equals to the sum of absolute differences between predicted value and actual future value.
 * <p>
 * Sample usage:
 * 
 * <pre>
 * // number of points from the past used to predict new one
 * int windowSize = 5;
 * // time series to predict
 * double[] data = new double[13] {1, 2, 4, 7, 11, 16, 22, 29, 37, 46, 56, 67, 79};
 * // constants
 * double[] constants = new double[10] {1, 2, 3, 5, 7, 11, 13, 17, 19, 23};
 * // create population
 * Population population = new Population(100, new GPTreeChromosome(new SimpleGeneFunction(windowSize + constants.Length)),
 *         new TimeSeriesPredictionFitness(data, windowSize, 1, constants), new EliteSelection());
 * // run one epoch of the population
 * population.RunEpoch();
 * </pre>
 */
public class TimeSeriesPredictionFitness implements IFitnessFunction {

    // time series data
    private double[] data;
    // varibles
    private double[] variables;
    // window size
    private int windowSize;
    // prediction size
    private int predictionSize;

    /**
     * Initializes a new instance of the {@link TimeSeriesPredictionFitness} class.
     * <p>
     * The <paramref name="data"/> parameter is a one dimensional array, which defines times
     * series to predict. The amount of learning samples is equal to the number of samples
     * in the provided time series, minus window size, minus prediction size.
     * <p>
     * The <paramref name="predictionSize"/> parameter specifies the amount of samples, which should
     * be excluded from training set. This set of samples may be used for future verification
     * of the prediction model.
     * <p>
     * The <paramref name="constants"/> parameter is an array of constants, which can be used as
     * additional variables for a genetic expression. The actual amount of variables for
     * genetic expression equals to the amount of constants plus the window size.
     * 
     * @param data Time series to be predicted.
     * @param windowSize Window size - number of past samples used to predict future value.
     * @param predictionSize Prediction size - number of values to be predicted. These values are excluded from training set.
     * @param constants Array of constants to be used as additional paramters for genetic expression.
     * 
     */
    public TimeSeriesPredictionFitness(double[] data, int windowSize, int predictionSize, double[] constants) {
        // check for correct parameters
        if (windowSize >= data.length)
            throw new IllegalArgumentException("Window size should be less then data amount");
        if (data.length - windowSize - predictionSize < 1)
            throw new IllegalArgumentException("Data size should be enough for window and prediction");
        // save parameters
        this.data = data;
        this.windowSize = windowSize;
        this.predictionSize = predictionSize;
        // copy constants
        variables = new double[constants.length + windowSize];
        System.arraycopy(constants, 0, variables, windowSize, constants.length);
    }

    @Override
    public double evaluate(IChromosome chromosome) {

        // get function in polish notation
        String function = chromosome.toString();

        // go through all the data
        double error = 0.0;
        for (int i = 0, n = data.length - windowSize - predictionSize; i < n; i++) {
            // put values from current window as variables
            for (int j = 0, b = i + windowSize - 1; j < windowSize; j++) {
                variables[j] = data[b - j];
            }

            // avoid evaluation errors
            try {
                // evaluate the function
                double y = PolishExpression.evaluate(function, variables);
                // check for correct numeric value
                if (Double.isNaN(y))
                return 0;
                // get the difference between evaluated value and
                // next value after the window, and sum error
                error += Math.abs(y - data[i + windowSize]);
            } catch (Exception e) {
                return 0;
            }
        }

        // return optimization function value
        return 100.0 / (error + 1);
    }

    /**
     * Translates genotype to phenotype.
     * <p>
     * The method returns string value, which represents prediction expression written in polish postfix notation.
     * <p>
     * The interpretation of the prediction expression is very simple. For example, let's take a look at sample expression, which was
     * received with window size equal to 5:
     * 
     * <pre>
     * $0 $1 - $5 / $2 *
     * </pre>
     * 
     * The above expression in postfix polish notation should be interpreted as a next expression:
     * 
     * <pre>
     * ((x[t - 1] - x[t - 2]) / const1) * x[t - 3]
     * </pre>
     * 
     * @param chromosome Chromosome, which genoteype should be translated to phenotype.
     * @return Returns chromosome's fenotype - the actual solution encoded by the chromosome.
     */
    public String translate(IChromosome chromosome) {
        // return polish notation for now ...
        return chromosome.toString();
    }
}
