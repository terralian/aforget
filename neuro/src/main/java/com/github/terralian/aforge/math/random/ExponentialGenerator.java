package com.github.terralian.aforge.math.random;

/**
 * Exponential random numbers generator.
 * <p>
 * The random number generator generates exponential
 * random numbers with specified rate value (lambda).
 * <p>
 * The generator uses {@link UniformOneGenerator} generator as a base
 * to generate random numbers.
 * <p>
 * Sample usage:
 * <pre>
 * // create instance of random generator
 * IRandomNumberGenerator generator = new ExponentialGenerator( 5 );
 * // generate random number
 * float randomNumber = generator.Next( );
 * </pre>
 */
public class ExponentialGenerator implements IRandomNumberGenerator {

    private UniformOneGenerator rand = null;

    private float rate = 0;

    /**
     * Initializes a new instance of the {@link ExponentialGenerator} class.
     * 
     * @param rate Rate value (inverse mean).
     * @throws IllegalArgumentException Rate value should be greater than zero.
     */
    public ExponentialGenerator(float rate) {
        this(rate, 0);
    }

    /**
     * Initializes a new instance of the {@link ExponentialGenerator} class.
     * 
     * @param rate Rate value (inverse mean).
     * @param seed Seed value to initialize random numbers generator.
     * @throws IllegalArgumentException Rate value should be greater than zero.
     */
    public ExponentialGenerator(float rate, int seed) {
        // check rate value
        if (rate <= 0)
            throw new IllegalArgumentException("Rate value should be greater than zero.");

        this.rand = new UniformOneGenerator(seed);
        this.rate = rate;
    }


    @Override
    public float getMean() {
        return 1.0f / rate;
    }

    @Override
    public float getVariance() {
        return 1f / (rate * rate);
    }

    @Override
    public float next() {
        return -(float) Math.log(rand.next()) / rate;
    }

    @Override
    public void setSeed(int seed) {
        rand = new UniformOneGenerator(seed);
    }

    /**
     * Rate value (inverse mean).
     * 
     * @return The rate value should be positive and non zero.
     */
    public float getRate() {
        return rate;
    }
}
