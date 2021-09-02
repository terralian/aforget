// AForge Math Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2007-2011
// contacts@aforgenet.com
//
package com.github.terralian.aforge.math.random;

import com.github.terralian.aforge.core.Range;

/**
 * Uniform random numbers generator.
 * <p>
 * The random numbers generator generates uniformly
 * distributed numbers in the <see cref="Range">specified range</see> - values
 * are greater or equal to minimum range's value and less than maximum range's
 * value.
 * <p>
 * The generator uses {@link UniformOneGenerator} generator
 * to generate random numbers.
 * <p>
 * Sample usage:
 * <pre>
 * // create instance of random generator
 * IRandomNumberGenerator generator = new UniformGenerator( new Range( 50, 100 ) );
 * // generate random number
 * float randomNumber = generator.Next( );
 * </pre>
 */
public class UniformGenerator implements IRandomNumberGenerator {

    private UniformOneGenerator rand = null;

    // generator's range
    private float min;
    private float length;

    /**
     * Initializes a new instance of the {@link UniformGenerator} class.
     * <p>
     * Initializes random numbers generator with zero seed.
     * 
     * @param range range Random numbers range.
     */
    public UniformGenerator(Range range) {
        this(range, 0);
    }

    /**
     * Initializes a new instance of the {@link UniformGenerator} class.
     * 
     * @param range Random numbers range.
     * @param seed Seed value to initialize random numbers generator.
     */
    public UniformGenerator(Range range, int seed) {
        rand = new UniformOneGenerator(seed);
        min = range.getMin();
        length = range.length();
    }

    @Override
    public float getMean() {
        return (min + min + length) / 2;
    }

    @Override
    public float getVariance() {
        return length * length / 12;
    }

    @Override
    public float next() {
        return (float) rand.next() * length + min;
    }

    @Override
    public void setSeed(int seed) {
        rand = new UniformOneGenerator(seed);
    }

}
