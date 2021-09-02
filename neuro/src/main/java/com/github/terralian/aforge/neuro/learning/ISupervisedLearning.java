// AForge Neural Net Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2007-2012
// contacts@aforgenet.com
//
package com.github.terralian.aforge.neuro.learning;

/**
 * Supervised learning interface.
 * <p>
 * The interface describes methods, which should be implemented
 * by all supervised learning algorithms. Supervised learning is such
 * type of learning algorithms, where system's desired output is known on
 * the learning stage. So, given sample input values and desired outputs,
 * system should adopt its internals to produce correct (or close to correct)
 * result after the learning step is complete.
 */
public interface ISupervisedLearning {

    /**
     * Runs learning iteration.
     * 
     * @param input Input vector.
     * @param output Desired output vector.
     * @return Returns learning error.
     */
    double run(double[] input, double[] output);

    /**
     * Runs learning epoch.
     * 
     * @param input Array of input vectors.
     * @param output Array of output vectors.
     * @return Returns sum of learning errors.
     */
    double runEpoch(double[][] input, double[][] output);
}
