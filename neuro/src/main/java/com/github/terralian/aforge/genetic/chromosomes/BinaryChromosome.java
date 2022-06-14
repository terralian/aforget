// AForge Genetic Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2006-2011
// contacts@aforgenet.com
//
package com.github.terralian.aforge.genetic.chromosomes;

import java.util.concurrent.ThreadLocalRandom;

import com.github.terralian.csharp.LangUtil;

/**
 * Binary chromosome, which supports length from 2 till 64.
 * <p>
 * The binary chromosome is the simplest type of chromosomes,
 * which is represented by a set of bits. Maximum number of bits comprising
 * the chromosome is 64.
 */
public class BinaryChromosome extends ChromosomeBase {

    /**
     * Chromosome's length in bits.
     */
    protected int length;

    /**
     * Numerical chromosome's value.
     */
    protected long val = 0L;

    /**
     * Random number generator for chromosoms generation, crossover, mutation, etc.
     */
    protected ThreadLocalRandom rand = ThreadLocalRandom.current();

    /**
     * Chromosome's maximum length.
     * <p>
     * Maximum possible numerical value, which may be represented
     * by the chromosome of current length.
     */
    public static final int MAX_LENGTH = 64;
    
    /**
     * Chromosome's length.
     * 
     * @return Length of the chromosome in bits.
     */
    public int getLength() {
        return length;
    }

    /**
     * Chromosome's value.
     * <p>
     * Current numerical value of the chromosome.
     */
    public long getValue() {
        return val;
    }

    /**
     * Max possible chromosome's value.
     * <p>
     * Maximum possible numerical value, which may be represented 
     * by the chromosome of current length.
     */
    public long matValue() {
        return Long.MAX_VALUE >> (64 - length);
    }
    
    /**
     * Initializes a new instance of the {@link BinaryChromosome} class.
     * 
     * @param length Chromosome's length in bits, [2, <see {@link #MAX_LENGTH}].
     */
    public BinaryChromosome(int length) {
        this.length = Math.max(2, Math.min(MAX_LENGTH, length));
        // randomize the chromosome
        generate();
    }
    
    /**
     * Initializes a new instance of the {@link BinaryChromosome} class.
     * <p>
     * This is a copy constructor, which creates the exact copy 
     * of specified chromosome.
     * 
     * @param source Source chromosome to copy.
     */
    protected BinaryChromosome(BinaryChromosome source) {
        length = source.length;
        val = source.val;
        fitness = source.fitness;
    }

    /**
     * Get string representation of the chromosome.
     * 
     * @return Returns string representation of the chromosome.
     */
    @Override
    public String toString() {
        long tval = val;
        char[] chars = new char[length];

        for (int i = length - 1; i >= 0; i--) {
            chars[i] = (char) ((tval & 1) + '0');
            tval >>= 1;
        }

        // return the result string
        return new String(chars);
    }

    /**
     * Generate random chromosome value.
     * <p>
     * Regenerates chromosome's value using random number generator.
     */
    @Override
    public void generate() {
        byte[] bytes = new byte[8];

        // generate value
        rand.nextBytes(bytes);
        val = LangUtil.toLong(bytes, false);
    }

    /**
     * Create new random chromosome with same parameters (factory method).
     * <p>
     * The method creates new chromosome of the same type, but randomly
     * initialized. The method is useful as factory method for those classes, which work
     * with chromosome's interface, but not with particular chromosome type.
     */
    @Override
    public IChromosome createNew() {
        return new BinaryChromosome(length);
    }

    /**
     * Clone the chromosome.
     * <p>
     * The method clones the chromosome returning the exact copy of it.
     * 
     * @return Return's clone of the chromosome.
     * 
     */
    @Override
    public IChromosome clone() {
        return new BinaryChromosome(this);
    }

    /**
     * Mutation operator.
     * <p>
     * The method performs chromosome's mutation, changing randomly
     * one of its bits.
     */
    @Override
    public void mutate() {
        val ^= ((long) 1 << rand.nextInt(length));

    }

    /**
     * Crossover operator.
     * <p>
     * The method performs crossover between two chromosomes � interchanging
     * range of bits between these chromosomes.
     * 
     * @param pair Pair chromosome to crossover with.
     */
    @Override
    public void crossover(IChromosome pair) {
        BinaryChromosome p = (BinaryChromosome) pair;

        // check for correct pair
        if ((p != null) && (p.length == length)) {
            int crossOverPoint = 63 - rand.nextInt(length - 1);
            long mask1 = Long.MAX_VALUE >> crossOverPoint;
            long mask2 = ~mask1;

            long v1 = val;
            long v2 = p.val;

            // calculate new values
            val = (v1 & mask1) | (v2 & mask2);
            p.val = (v2 & mask1) | (v1 & mask2);
        }
    }
}
