// AForge Genetic Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2006-2011
// contacts@aforgenet.com
//
package com.github.terralian.aforge.genetic.chromosomes;

import java.util.concurrent.ThreadLocalRandom;

import com.github.terralian.aforge.math.random.IRandomNumberGenerator;

/**
 * Double array chromosome.
 * <p>
 * Double array chromosome represents array of double values.
 * Array length is in the range of [2, 65536].
 * <p>
 * See documentation to {@link #mutate} and {@link #crossover} methods
 * for information regarding implemented mutation and crossover operators.</para>
 */
public class DoubleArrayChromosome extends ChromosomeBase {
    /**
     * Chromosome generator.
     * <p>
     * This random number generator is used to initialize chromosome's genes,
     * which is done by calling {@link #generate()} method.
     */
    protected IRandomNumberGenerator chromosomeGenerator;
    
    /**
     * Mutation multiplier generator.
     * <p>
     * This random number generator is used to generate random multiplier values,
     * which are used to multiply chromosome's genes during mutation.
     */
    protected IRandomNumberGenerator mutationMultiplierGenerator;

    /**
     * Mutation addition generator.
     * <p>
     * This random number generator is used to generate random addition values,
     * which are used to add to chromosome's genes during mutation.
     */
    protected IRandomNumberGenerator mutationAdditionGenerator;

    /**
     * Random number generator for crossover and mutation points selection.
     * <p>
     * This random number generator is used to select crossover and mutation points.
     */
    protected static ThreadLocalRandom rand = ThreadLocalRandom.current();

    /**
     * Chromosome's maximum length.
     * <p>
     * Maxim chromosome's length in array elements.
     */
    public final int maxLength = 65536;

    /**
     * Chromosome's length in number of elements.
     */
    private int length;

    /**
     * Chromosome's value.
     */
    protected double[] val = null;

    // balancers to control type of mutation and crossover
    private double mutationBalancer = 0.5;
    private double crossoverBalancer = 0.5;

    /**
     * Initializes a new instance of the {@link DoubleArrayChromosome} class.
     * <p>
     * The constructor initializes the new chromosome randomly by calling {@link #generate()} method.
     * 
     * @param chromosomeGenerator Chromosome generator - random number generator, which is used to initialize chromosome's genes, which is
     *        done by calling {@link #generate} method or in class constructor.
     * @param mutationMultiplierGenerator Mutation multiplier generator - random number generator, which is used to generate random
     *        multiplier values, which are used to multiply chromosome's genes during mutation.
     * @param mutationAdditionGenerator Mutation addition generator - random number generator, which is used to generate random addition
     *        values, which are used to add to chromosome's genes during mutation.
     * @param length Chromosome's length in array elements, [2, {@link #maxLength}].
     */
    public DoubleArrayChromosome(IRandomNumberGenerator chromosomeGenerator, IRandomNumberGenerator mutationMultiplierGenerator,
            IRandomNumberGenerator mutationAdditionGenerator, int length) {

        // save parameters
        this.chromosomeGenerator = chromosomeGenerator;
        this.mutationMultiplierGenerator = mutationMultiplierGenerator;
        this.mutationAdditionGenerator = mutationAdditionGenerator;
        this.length = Math.max(2, Math.min(maxLength, length));;

        // allocate array
        val = new double[length];

        // generate random chromosome
        generate();
    }

    /**
     * Initializes a new instance of the {@link DoubleArrayChromosome} class.
     * <p>
     * The constructor initializes the new chromosome with specified values
     * 
     * @param chromosomeGenerator Chromosome generator - random number generator, which is used to initialize chromosome's genes, which is
     *        done by calling {@link #generate} method or in class constructor.
     * @param mutationMultiplierGenerator Mutation multiplier generator - random number generator, which is used to generate random
     *        multiplier values, which are used to multiply chromosome's genes during mutation.
     * @param mutationAdditionGenerator Mutation addition generator - random number generator, which is used to generate random addition
     *        values, which are used to add to chromosome's genes during mutation.
     * @param values Values used to initialize the chromosome.
     * @throws IndexOutOfBoundsException Invalid length of values array.
     */
    public DoubleArrayChromosome(IRandomNumberGenerator chromosomeGenerator, IRandomNumberGenerator mutationMultiplierGenerator,
            IRandomNumberGenerator mutationAdditionGenerator, double[] values) {
        if ((values.length < 2) || (values.length > maxLength))
            throw new IndexOutOfBoundsException("Invalid length of values array.");

        // save parameters
        this.chromosomeGenerator = chromosomeGenerator;
        this.mutationMultiplierGenerator = mutationMultiplierGenerator;
        this.mutationAdditionGenerator = mutationAdditionGenerator;
        this.length = values.length;

        // copy specified values
        val = (double[]) values.clone();
    }

    /**
     * Initializes a new instance of the {@link #DoubleArrayChromosome} class.
     * <p>
     * This is a copy constructor, which creates the exact copy of specified chromosome.
     * 
     * @param source Source chromosome to copy.
     */
    public DoubleArrayChromosome(DoubleArrayChromosome source) {
        this.chromosomeGenerator = source.chromosomeGenerator;
        this.mutationMultiplierGenerator = source.mutationMultiplierGenerator;
        this.mutationAdditionGenerator = source.mutationAdditionGenerator;
        this.length = source.length;
        this.fitness = source.fitness;
        this.mutationBalancer = source.mutationBalancer;
        this.crossoverBalancer = source.crossoverBalancer;

        // copy genes
        val = (double[]) source.val.clone();
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
        for (int i = 0; i < length; i++) {
            // generate next value
            val[i] = chromosomeGenerator.next();
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
        return new DoubleArrayChromosome(chromosomeGenerator, mutationMultiplierGenerator, mutationAdditionGenerator, length);
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
        return new DoubleArrayChromosome(this);
    }

    /**
     * Mutation operator.
     * <p>
     * The method performs chromosome's mutation, adding random number
     * to chromosome's gene or multiplying the gene by random number. These random
     * numbers are generated with help of {@link #mutationMultiplierGenerator} and {@link #mutationAdditionGenerator} generators.
     * 
     * <p>The exact type of mutation applied to the particular gene
     * is selected randomly each time and depends on {@link #mutationBalancer}
     * Before mutation is done a random number is generated in [0, 1] range - if the
     * random number is smaller than {@link #mutationBalancer}, then multiplication
     * mutation is done, otherwise addition mutation.
     */
    @Override
    public void mutate() {
        int mutationGene = rand.nextInt(length);

        if (rand.nextDouble() < mutationBalancer) {
            val[mutationGene] *= mutationMultiplierGenerator.next();
        } else {
            val[mutationGene] += mutationAdditionGenerator.next();
        }
    }

    /**
     * Crossover operator.
     * 
     * <p>The method performs crossover between two chromosomes, selecting
     * randomly the exact type of crossover to perform, which depends on {@link #crossoverBalancer}
     * Before crossover is done a random number is generated in [0, 1] range - if the
     * random number is smaller than {@link #crossoverBalancer}, then the first crossover
     * type is used, otherwise second type is used.
     * 
     * <p>The <b>first crossover type</b> is based on interchanging
     * range of genes (array elements) between these chromosomes and is known
     * as one point crossover. A crossover point is selected randomly and chromosomes
     * interchange genes, which start from the selected point.
     * 
     * <p>The <b>second crossover type</b> is aimed to produce one child, which genes'
     * values are between corresponding genes of parents, and another child, which genes'
     * values are outside of the range formed by corresponding genes of parents. 
     * Let take, for example, two genes with 1.0 and 3.0 valueû (of course chromosomes have
     * more genes, but for simplicity lets think about one). First of all we randomly choose
     * a factor in the [0, 1] range, let's take 0.4. Then, for each pair of genes (we have
     * one pair) we calculate difference value, which is 2.0 in our case. In the result we�ll
     * have two children � one between and one outside of the range formed by parents genes' values.
     * We may have 1.8 and 3.8 children, or we may have 0.2 and 2.2 children. As we can see
     * we add/subtract (chosen randomly) <i>difference * factor</i>. So, this gives us exploration
     * in between and in near outside. The randomly chosen factor is applied to all genes
     * of the chromosomes participating in crossover.
     * 
     * @param pair Pair chromosome to crossover with.
     */
    @Override
    public void crossover(IChromosome pair) {
        DoubleArrayChromosome p = (DoubleArrayChromosome) pair;

        // check for correct pair
        if ((p != null) && (p.length == length)) {
            if (rand.nextDouble() < crossoverBalancer) {
                // crossover point
                int crossOverPoint = rand.nextInt(length - 1) + 1;
                // length of chromosome to be crossed
                int crossOverLength = length - crossOverPoint;
                // temporary array
                double[] temp = new double[crossOverLength];

                // copy part of first (this) chromosome to temp
                System.arraycopy(val, crossOverPoint, temp, 0, crossOverLength);
                // copy part of second (pair) chromosome to the first
                System.arraycopy(p.val, crossOverPoint, val, crossOverPoint, crossOverLength);
                // copy temp to the second
                System.arraycopy(temp, 0, p.val, crossOverPoint, crossOverLength);
            } else {
                double[] pairVal = p.val;

                double factor = rand.nextDouble();
                if (rand.nextInt(2) == 0)
                    factor = -factor;

                for (int i = 0; i < length; i++) {
                    double portion = (val[i] - pairVal[i]) * factor;

                    val[i] -= portion;
                    pairVal[i] += portion;
                }
            }
        }
    }

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
     * <p>
     * Current value of the chromosome.
     */
    public double[] getValue() {
        return val;
    }
    
    /**
     * Mutation balancer to control mutation type, [0, 1].
     * <p>
     * The property controls type of mutation, which is used more
     * frequently. A random number is generated each time before doing mutation -
     * if the random number is smaller than the specified balance value, then one
     * mutation type is used, otherwse another. See {@link #mutate()} method
     * for more information.
     * <p>
     * Default value is set to <b>0.5</b>.
     */
    public double getMutationBalancer() {
        return mutationBalancer;
    }

    /**
     * Mutation balancer to control mutation type, [0, 1].
     * <p>
     * The property controls type of mutation, which is used more
     * frequently. A random number is generated each time before doing mutation -
     * if the random number is smaller than the specified balance value, then one
     * mutation type is used, otherwse another. See {@link #mutate()} method
     * for more information.
     * <p>
     * Default value is set to <b>0.5</b>.
     */
    public void setMutationBalancer(double mutationBalancer) {
        this.mutationBalancer = mutationBalancer;
    }

    /**
     * Crossover balancer to control crossover type, [0, 1].
     * <p>
     * The property controls type of crossover, which is used more
     * frequently. A random number is generated each time before doing crossover -
     * if the random number is smaller than the specified balance value, then one
     * crossover type is used, otherwse another. See {@link #crossover} method
     * for more information.
     * <p>
     * Default value is set to <b>0.5</b>.
     */
    public double getCrossoverBalancer() {
        return crossoverBalancer;
    }

    /**
     * Crossover balancer to control crossover type, [0, 1].
     * <p>
     * The property controls type of crossover, which is used more
     * frequently. A random number is generated each time before doing crossover -
     * if the random number is smaller than the specified balance value, then one
     * crossover type is used, otherwse another. See {@link #crossover} method
     * for more information.
     * <p>
     * Default value is set to <b>0.5</b>.
     */
    public void setCrossoverBalancer(double crossoverBalancer) {
        this.crossoverBalancer = crossoverBalancer;
    }
}
