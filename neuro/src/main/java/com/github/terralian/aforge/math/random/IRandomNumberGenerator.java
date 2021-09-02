// AForge Math Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2007-2011
// contacts@aforgenet.com
//
package com.github.terralian.aforge.math.random;

/**
 * Interface for random numbers generators.
 * <p>
 * The interface defines set of methods and properties, which should
 * be implemented by different algorithms for random numbers generatation.
 */
public interface IRandomNumberGenerator {

    /**
     * Mean value of generator.
     */
    float getMean();

    /**
     * Variance value of generator.
     */
    float getVariance();

    /**
     * Generate next random number.
     * 
     * @return Returns next random number.
     */
    float next();

    /**
     * Set seed of the random numbers generator.
     * 
     * @param seed Seed value.
     */
    void setSeed(int seed);
}
