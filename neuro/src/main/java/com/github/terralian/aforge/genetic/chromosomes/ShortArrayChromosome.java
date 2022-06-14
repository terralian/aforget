// AForge Genetic Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2006-2011
// contacts@aforgenet.com
//
package com.github.terralian.aforge.genetic.chromosomes;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Short array chromosome.
 * <p>
 * Short array chromosome represents array of unsigned short values.
 * Array length is in the range of [2, 65536].
 */
public class ShortArrayChromosome extends ChromosomeBase {

    /**
     * Chromosome's length in number of elements.
     */
    protected int length;

    /**
     * Maximum value of chromosome's gene (element).
     */
    protected int maxValue;

    /**
     * Chromosome's value.
     */
    protected short[] val = null;

    /**
     * Random number generator for chromosoms generation, crossover, mutation, etc.
     */
    protected ThreadLocalRandom rand = ThreadLocalRandom.current();

    /**
     * Chromosome's maximum length.
     * <p>
     * Maxim chromosome's length in array elements.
     */
    public static final int MaxLength = Short.MAX_VALUE;

    /**
     * Chromosome's length.
     * <p>
     * Length of the chromosome in array elements.
     */
    public int getLength() {
        return length;
    }

    /**
     * Chromosome's value.
     * 
     * @return Current value of the chromosome.
     */
    public short[] getValue() {
        return val;
    }

    /**
     * Max possible value of single chromosomes element - gene.
     * <p>
     * Maximum possible numerical value, which may be represented
     * by single chromosome's gene (array element).
     */
    public int getMaxValue() {
        return maxValue;
    }

    /**
     * Initializes a new instance of the {@link ShortArrayChromosome} class.
     * <p>
     * This constructor initializes chromosome setting genes' maximum value to
     * maximum posible value of {@link Short} type.
     * 
     * @param length Chromosome's length in array elements, [2, {@link ShortArrayChromosome#MaxLength}].
     */
    public ShortArrayChromosome(int length) {
        this(length, Short.MAX_VALUE);
    }

    /**
     * Initializes a new instance of the {@link ShortArrayChromosome} class.
     * <p>
     * This constructor initializes chromosome setting genes' maximum value to
     * maximum posible value of {@link Short} type.
     * 
     * @param length Chromosome's length in array elements, [2, {@link ShortArrayChromosome#MaxLength}].
     * @param maxValue Maximum value of chromosome's gene (array element).
     */
    public ShortArrayChromosome(int length, int maxValue) {
        // save parameters
        this.length = Math.max(2, Math.min(MaxLength, length));
        this.maxValue = Math.max(1, Math.min(Short.MAX_VALUE, maxValue));

        // allocate array
        val = new short[this.length];

        // generate random chromosome
        generate();
    }

    /**
     * Initializes a new instance of the {@link ShortArrayChromosome} class.
     * <p>
     * This is a copy constructor, which creates the exact copy
     * of specified chromosome.
     * 
     * @param source Source chromosome to copy.
     */
    protected ShortArrayChromosome(ShortArrayChromosome source) {
        // copy all properties
        length = source.length;
        maxValue = source.maxValue;
        val = source.val.clone();
        fitness = source.fitness;
    }
    
    /**
     * Get string representation of the chromosome.
     * 
     * @return Returns string representation of the chromosome.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // append first gene
        sb.append(val[0]);
        // append all other genes
        for (int i = 1; i < length; i++) {
            sb.append(' ');
            sb.append(val[i]);
        }
        return sb.toString();
    }

    /**
     * Generate random chromosome value.
     * <p>
     * Regenerates chromosome's value using random number generator.
     */
    @Override
    public void generate() {
        int max = maxValue + 1;

        for (int i = 0; i < length; i++) {
            // generate next value
            val[i] = (short) rand.nextInt(max);
        }
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
        return new ShortArrayChromosome(length, maxValue);
    }

    /**
     * Clone the chromosome.
     * <p>
     * The method clones the chromosome returning the exact copy of it.
     * 
     * @return Return's clone of the chromosome.
     */
    @Override
    public IChromosome clone() {
        return new ShortArrayChromosome(this);
    }

    /**
     * Mutation operator.
     * <p>
     * The method performs chromosome's mutation, changing randomly
     * one of its genes (array elements).
     */
    @Override
    public void mutate() {
        // get random index
        int i = rand.nextInt(length);
        // randomize the gene
        val[i] = (short) rand.nextInt(maxValue + 1);
    }

    /**
     * Crossover operator.
     * <p>
     * The method performs crossover between two chromosomes � interchanging some parts of chromosomes.
     * 
     * @param pair Pair chromosome to crossover with.
     */
    @Override
    public void crossover(IChromosome pair) {
        ShortArrayChromosome p = (ShortArrayChromosome) pair;

        // check for correct pair
        if ((p != null) && (p.length == length)) {
            // crossover point
            int crossOverPoint = rand.nextInt(length - 1) + 1;
            // length of chromosome to be crossed
            int crossOverLength = length - crossOverPoint;
            // temporary array
            Short[] temp = new Short[crossOverLength];

            // copy part of first (this) chromosome to temp
            System.arraycopy(val, crossOverPoint, temp, 0, crossOverLength);
            // copy part of second (pair) chromosome to the first
            System.arraycopy(p.val, crossOverPoint, val, crossOverPoint, crossOverLength);
            // copy temp to the second
            System.arraycopy(temp, 0, p.val, crossOverPoint, crossOverLength);
        }
    }

}
