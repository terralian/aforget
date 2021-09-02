// AForge Math Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2007-2011
// contacts@aforgenet.com
//
package com.github.terralian.aforge.math.random;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Uniform random numbers generator in the range of [0, 1).
 * <p>
 * The random number generator generates uniformly
 * distributed numbers in the range of [0, 1) - greater or equal to 0.0
 * and less than 1.0.
 * <p>
 * At this point the generator is based on the
 * internal .NET generator, but may be rewritten to
 * use faster generation algorithm.
 * <p>
 * Sample usage:
 * <pre>
 * // create instance of random generator
 * IRandomNumberGenerator generator = new UniformOneGenerator( );
 * // generate random number
 * float randomNumber = generator.Next( );
 * </pre>
 */
public class UniformOneGenerator implements IRandomNumberGenerator {

    // .NET random generator as a base
    private ThreadLocalRandom rand = ThreadLocalRandom.current();

    /**
     * Initializes a new instance of the {@link UniformOneGenerator} class.
     * <p>
     * Initializes random numbers generator with zero seed.
     */
    public UniformOneGenerator() {
        rand.setSeed(0);
    }
    
    /**
     * Initializes a new instance of the {@link UniformOneGenerator} class.
     * 
     * @param seed Seed value to initialize random numbers generator.
     */
    public UniformOneGenerator(int seed) {
        setSeed(seed);
    }

    @Override
    public float getMean() {
        return 0.5f;
    }

    @Override
    public float getVariance() {
        return 1f / 12;
    }

    @Override
    public float next() {
        return (float) rand.nextDouble();
    }

    /**
     * Set seed of the random numbers generator.
     * <p>
     * Resets random numbers generator initializing it with specified seed value.
     */
    @Override
    public void setSeed(int seed) {
        rand.setSeed(seed);
    }

}
