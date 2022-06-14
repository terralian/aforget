// AForge Genetic Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2006-2010
// contacts@aforgenet.com
//
package com.github.terralian.aforge.genetic.chromosomes;

/**
 * Permutation chromosome.
 * <p>
 * Permutation chromosome is based on short array chromosome, but has two features:
 * <ul>
 * <li>all genes are unique within chromosome, i.e. there are no two genes with the same value;
 * <li>maximum value of each gene is equal to chromosome length minus 1.
 * </ul>
 */
public class PermutationChromosome extends ShortArrayChromosome {

    /**
     * Initializes a new instance of the {@link PermutationChromosome} class.
     */
    public PermutationChromosome(int length) {
        super(length, length - 1);
    }

    /**
     * Initializes a new instance of the {@link PermutationChromosome} class.
     * <p>
     * This is a copy constructor, which creates the exact copy
     * of specified chromosome.
     * 
     * @param source Source chromosome to copy.
     */
    protected PermutationChromosome(PermutationChromosome source) {
        super(source);
    }

    /**
     * Generate random chromosome value.
     * <p>
     * Regenerates chromosome's value using random number generator.
     */
    @Override
    public void generate() {
        // create ascending permutation initially
        for (int i = 0; i < length; i++) {
            val[i] = (short) i;
        }

        // shufle the permutation
        for (int i = 0, n = length >> 1; i < n; i++) {
            short t;
            int j1 = rand.nextInt(length);
            int j2 = rand.nextInt(length);

            // swap values
            t = val[j1];
            val[j1] = val[j2];
            val[j2] = t;
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
        return new PermutationChromosome(length);
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
        return new PermutationChromosome(this);
    }
    
    /**
     * Mutation operator.
     * <p>
     * The method performs chromosome's mutation, changing randomly
     * one of its genes (array elements).
     */
    @Override
    public void mutate() {
        short t;
        int j1 = rand.nextInt(length);
        int j2 = rand.nextInt(length);

        // swap values
        t = val[j1];
        val[j1] = val[j2];
        val[j2] = t;
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
        PermutationChromosome p = (PermutationChromosome) pair;

        // check for correct pair
        if ((p != null) && (p.length == length)) {
            short[] child1 = new short[length];
            short[] child2 = new short[length];

            // create two children
            createChildUsingCrossover(this.val, p.val, child1);
            createChildUsingCrossover(p.val, this.val, child2);

            // replace parents with children
            this.val = child1;
            p.val = child2;
        }
    }

    // Produce new child applying crossover to two parents
    private void createChildUsingCrossover(short[] parent1, short[] parent2, short[] child) {
        short[] indexDictionary1 = createIndexDictionary(parent1);
        short[] indexDictionary2 = createIndexDictionary(parent2);

        // temporary array to specify if certain gene already
        // present in the child
        boolean[] geneIsBusy = new boolean[length];
        // previous gene in the child and two next candidates
        short prev, next1, next2;
        // candidates validness - candidate is valid, if it is not
        // yet in the child
        boolean valid1, valid2;

        int j, k = length - 1;

        // first gene of the child is taken from the second parent
        prev = child[0] = parent2[0];
        geneIsBusy[prev] = true;

        // resolve all other genes of the child
        for (int i = 1; i < length; i++) {
            // find the next gene after PREV in both parents
            // 1
            j = indexDictionary1[prev];
            next1 = (j == k) ? parent1[0] : parent1[j + 1];
            // 2
            j = indexDictionary2[prev];
            next2 = (j == k) ? parent2[0] : parent2[j + 1];

            // check candidate genes for validness
            valid1 = !geneIsBusy[next1];
            valid2 = !geneIsBusy[next2];

            // select gene
            if (valid1 && valid2) {
                // both candidates are valid
                // select one of theme randomly
                prev = (rand.nextInt(2) == 0) ? next1 : next2;
            } else if (!(valid1 || valid2)) {
                // none of candidates is valid, so
                // select random gene which is not in the child yet
                int r = j = rand.nextInt(length);

                // go down first
                while ((r < length) && (geneIsBusy[r] == true))
                    r++;
                if (r == length) {
                    // not found, try to go up
                    r = j - 1;
                    while (geneIsBusy[r] == true) // && ( r >= 0 )
                        r--;
                }
                prev = (short) r;
            } else {
                // one of candidates is valid
                prev = (valid1) ? next1 : next2;
            }

            child[i] = prev;
            geneIsBusy[prev] = true;
        }
    }

    // Create dictionary for fast lookup of genes' indexes
    private static short[] createIndexDictionary(short[] genes) {
        short[] indexDictionary = new short[genes.length];

        for (int i = 0, n = genes.length; i < n; i++) {
            indexDictionary[genes[i]] = (short) i;
        }

        return indexDictionary;
    }
}
