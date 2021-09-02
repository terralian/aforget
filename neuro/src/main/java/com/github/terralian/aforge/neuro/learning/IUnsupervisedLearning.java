// AForge Neural Net Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2007-2012
// contacts@aforgenet.com
//
package com.github.terralian.aforge.neuro.learning;

/**
 * Unsupervised learning interface.
 * <p>
 * <remarks>The interface describes methods, which should be implemented
 * by all unsupervised learning algorithms. Unsupervised learning is such
 * type of learning algorithms, where system's desired output is not known on
 * the learning stage. Given sample input values, it is expected, that
 * system will organize itself in the way to find similarities betweed provided
 * samples.</remarks>
 */
public interface IUnsupervisedLearning {

    /**
     * Runs learning iteration.
     * 
     * @param input Input vector.
     * @return Returns learning error.
     */
    double run(double[] input);

    /**
     * Runs learning epoch.
     * 
     * @param input Array of input vectors.
     * @return Returns sum of learning errors.
     */
    double runEpoch(double[][] input);
}
