// AForge Genetic Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2006-2011
// contacts@aforgenet.com
//
package com.github.terralian.aforge.genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.github.terralian.aforge.genetic.chromosomes.IChromosome;
import com.github.terralian.aforge.genetic.fitness.IFitnessFunction;
import com.github.terralian.aforge.genetic.selection.ISelectionMethod;
import com.github.terralian.csharp.LangUtil;

/**
 * Population of chromosomes.
 * <p>
 * The class represents population - collection of individuals (chromosomes)
 * and provides functionality for common population's life cycle - population growing
 * with help of genetic operators and selection of chromosomes to new generation
 * with help of selection algorithm. The class may work with any type of chromosomes
 * implementing {@link IChromosome} interface, use any type of fitness functions
 * implementing {@link IFitnessFunction} interface and use any type of selection
 * algorithms implementing {@link ISelectionMethod} interface.
 * 
 */
public class Population {

    private IFitnessFunction fitnessFunction;
    private ISelectionMethod selectionMethod;
    private List<IChromosome> population = new ArrayList<>();
    private int size;
    private double randomSelectionPortion = 0.0;
    private boolean autoShuffling = false;

    // population parameters
    private double crossoverRate = 0.75;
    private double mutationRate = 0.10;

    // random number generator
    private static ThreadLocalRandom rand = ThreadLocalRandom.current();

    //
    private double fitnessMax = 0;
    private double fitnessSum = 0;
    private double fitnessAvg = 0;
    private IChromosome bestChromosome = null;

    /**
     * Initializes a new instance of the {@link Population} class.
     * <p>
     * Creates new population of specified size. The specified ancestor
     * becomes first member of the population and is used to create other members
     * with same parameters, which were used for ancestor's creation.
     * 
     * @param size Initial size of population.
     * @param ancestor Ancestor chromosome to use for population creatioin.
     * @param fitnessFunction Fitness function to use for calculating chromosome's fitness values.
     * @param selectionMethod Selection algorithm to use for selection chromosome's to new generation.
     * @throws IllegalArgumentException Too small population's size was specified. The exception is thrown in the case if size is smaller than 2.
     */
    public Population(int size, IChromosome ancestor, IFitnessFunction fitnessFunction, ISelectionMethod selectionMethod) {
        if (size < 2)
            throw new IllegalArgumentException("Too small population's size was specified.");

        this.fitnessFunction = fitnessFunction;
        this.selectionMethod = selectionMethod;
        this.size = size;

        // add ancestor to the population
        ancestor.evaluate(fitnessFunction);
        population.add(ancestor.clone());
        // add more chromosomes to the population
        for (int i = 1; i < size; i++) {
            // create new chromosome
            IChromosome c = ancestor.createNew();
            // calculate it's fitness
            c.evaluate(fitnessFunction);
            // add it to population
            population.add(c);
        }
    }

    /**
     * Regenerate population.
     * <p>
     * The method regenerates population filling it with random chromosomes.
     */
    public void regenerate() {
        IChromosome ancestor = population.get(0);

        // clear population
        population.clear();
        // add chromosomes to the population
        for (int i = 0; i < size; i++) {
            // create new chromosome
            IChromosome c = ancestor.createNew();
            // calculate it's fitness
            c.evaluate(fitnessFunction);
            // add it to population
            population.add(c);
        }
    }

    /**
     * Do crossover in the population.
     * <p>
     * The method walks through the population and performs crossover operator
     * taking each two chromosomes in the order of their presence in the population.
     * The total amount of paired chromosomes is determined by {@link #crossoverRate}
     */
    public void crossover() {
        // crossover
        for (int i = 1; i < size; i += 2) {
            // generate next random number and check if we need to do crossover
            if (rand.nextDouble() <= crossoverRate) {
                // clone both ancestors
                IChromosome c1 = population.get(i - 1).clone();
                IChromosome c2 = population.get(i).clone();

                // do crossover
                c1.crossover(c2);

                // calculate fitness of these two offsprings
                c1.evaluate(fitnessFunction);
                c2.evaluate(fitnessFunction);

                // add two new offsprings to the population
                population.add(c1);
                population.add(c2);
            }
        }
    }

    /**
     * Do mutation in the population.
     * <p>
     * The method walks through the population and performs mutation operator
     * taking each chromosome one by one. The total amount of mutated chromosomes is
     * determined by {@link #mutationRate}.
     */
    public void mutate() {
        // mutate
        for (int i = 0; i < size; i++) {
            // generate next random number and check if we need to do mutation
            if (rand.nextDouble() <= mutationRate) {
                // clone the chromosome
                IChromosome c = population.get(i).clone();
                // mutate it
                c.mutate();
                // calculate fitness of the mutant
                c.evaluate(fitnessFunction);
                // add mutant to the population
                population.add(c);
            }
        }
    }

    /**
     * Do selection.<p>
     * The method applies selection operator to the current population. Using
     * specified selection algorithm it selects members to the new generation from current
     * generates and adds certain amount of random members, if is required
     * (see <see cref="RandomSelectionPortion"/>).
     */
    public void selection() {
        // amount of random chromosomes in the new population
        int randomAmount = (int) (randomSelectionPortion * size);

        // do selection
        selectionMethod.applySelection(population, size - randomAmount);

        // add random chromosomes
        if (randomAmount > 0) {
            IChromosome ancestor = population.get(0);

            for (int i = 0; i < randomAmount; i++) {
                // create new chromosome
                IChromosome c = ancestor.createNew();
                // calculate it's fitness
                c.evaluate(fitnessFunction);
                // add it to population
                population.add(c);
            }
        }

        findBestChromosome();
    }

    /**
     * Run one epoch of the population.
     * <p>
     * The method runs one epoch of the population, doing crossover, mutation
     * and selection by calling {@link #crossover()}, {@link #mutate()} and
     * {@link #selection()}.
     */
    public void runEpoch() {
        crossover();
        mutate();
        selection();

        if (autoShuffling)
            shuffle();
    }

    /**
     * Shuffle randomly current population.
     * <p>
     * Population shuffling may be useful in cases when selection
     * operator results in not random order of chromosomes (for example, after elite
     * selection population may be ordered in ascending/descending order).
     */
    public void shuffle() {
        // current population size
        int size = population.size();
        // create temporary copy of the population
        List<IChromosome> tempPopulation = population.subList(0, size);
        // clear current population and refill it randomly
        population.clear();

        while (size > 0) {
            int i = rand.nextInt(size);

            population.add(tempPopulation.get(i));
            tempPopulation.remove(i);

            size--;
        }
    }

    /**
     * Add chromosome to the population.
     * <p>
     * The method adds specified chromosome to the current population.
     * Manual adding of chromosome maybe useful, when it is required to add some initialized
     * chromosomes instead of random.
     * <p>
     * Adding chromosome manually should be done very carefully, since it
     * may break the population. The manually added chromosome must have the same type
     * and initialization parameters as the ancestor passed to constructor.
     * @param chromosome Chromosome to add to the population.
     */
    public void addChromosome(IChromosome chromosome) {
        chromosome.evaluate(fitnessFunction);
        population.add(chromosome);
    }

    /**
     * Perform migration between two populations.
     * <p>
     * The method performs migration between two populations - current and the anotherPopulation.
     * During migration numberOfMigrants of chromosomes is choosen from each population using
     * migrantsSelector and put into another population replacing worst members
     * there.
     * 
     * @param anotherPopulation Population to do migration with.
     * @param numberOfMigrants Number of chromosomes from each population to migrate.
     * @param migrantsSelector Selection algorithm used to select chromosomes to migrate.
     */
    public void migrate(Population anotherPopulation, int numberOfMigrants, ISelectionMethod migrantsSelector) {
        int currentSize = this.size;
        int anotherSize = anotherPopulation.size;

        // create copy of current population
        List<IChromosome> currentCopy = new ArrayList<>();

        for (int i = 0; i < currentSize; i++) {
            currentCopy.add(population.get(i).clone());
        }

        // create copy of another population
        List<IChromosome> anotherCopy = new ArrayList<>();

        for (int i = 0; i < anotherSize; i++) {
            anotherCopy.add(anotherPopulation.population.get(i).clone());
        }

        // apply selection to both populations' copies - select members to migrate
        migrantsSelector.applySelection(currentCopy, numberOfMigrants);
        migrantsSelector.applySelection(anotherCopy, numberOfMigrants);

        // sort original populations, so the best chromosomes are in the beginning
        population.sort((a, b) -> a.compareTo(b));
        anotherPopulation.population.sort((a, b) -> a.compareTo(b));

        // remove worst chromosomes from both populations to free space for new members
        LangUtil.listRemoveRange(population, currentSize - numberOfMigrants, numberOfMigrants);
        LangUtil.listRemoveRange(anotherPopulation.population, anotherSize - numberOfMigrants, numberOfMigrants);

        // put migrants to corresponding populations
        population.addAll(anotherCopy);
        anotherPopulation.population.addAll(currentCopy);

        // find best chromosomes in each population
        findBestChromosome();
        anotherPopulation.findBestChromosome();
    }

    /**
     * Resize population to the new specified size.
     * <p>
     * The method does resizing of population. In the case if population should grow, it just adds missing number of random members. In the
     * case if population should get smaller, the {@link #selectionMethod} is used to reduce the population.
     * 
     * @param newPopulationSize New size of population.
     * @throws IllegalArgumentException Too small population's size was specified. The exception is thrown in the case if newPopulationSize
     *         is smaller than 2.
     */
    public void resize(int newPopulationSize) {
        resize(newPopulationSize, selectionMethod);
    }

    /**
     * Resize population to the new specified size.
     * <p>
     * The method does resizing of population. In the case if population should grow, it just adds missing number of random members. In the
     * case if population should get smaller, the specified selection method is used to reduce the population.
     * 
     * @param newPopulationSize New size of population.
     * @param membersSelector Selection algorithm to use in the case if population should get smaller.
     * @throws IllegalArgumentException Too small population's size was specified. The exception is thrown in the case if newPopulationSize
     *         is smaller than 2.
     */
    public void resize(int newPopulationSize, ISelectionMethod membersSelector) {
        if (newPopulationSize < 2)
            throw new IllegalArgumentException("Too small new population's size was specified.");

        if (newPopulationSize > size) {
            // population is growing, so add new rundom members

            // Note: we use population.Count here instead of "size" because
            // population may be bigger already after crossover/mutation. So
            // we just keep those members instead of adding random member.
            int toAdd = newPopulationSize - population.size();

            for (int i = 0; i < toAdd; i++) {
                // create new chromosome
                IChromosome c = population.get(0).createNew();
                // calculate it's fitness
                c.evaluate(fitnessFunction);
                // add it to population
                population.add(c);
            }
        } else {
            // do selection
            membersSelector.applySelection(population, newPopulationSize);
        }

        size = newPopulationSize;
    }

    // Find best chromosome in the population so far
    private void findBestChromosome() {
        bestChromosome = population.get(0);
        fitnessMax = bestChromosome.getFitness();
        fitnessSum = fitnessMax;

        for (int i = 1; i < size; i++) {
            double fitness = population.get(i).getFitness();

            // accumulate summary value
            fitnessSum += fitness;

            // check for max
            if (fitness > fitnessMax) {
                fitnessMax = fitness;
                bestChromosome = population.get(i);
            }
        }
        fitnessAvg = fitnessSum / size;
    }

    /**
     * Crossover rate, [0.1, 1].
     * <p>
     * The value determines the amount of chromosomes which participate
     * <p>
     * Default value is set to <b>0.75</b>.
     */
    public double getCrossoverRate() {
        return crossoverRate;
    }

    /**
     * Crossover rate, [0.1, 1].
     * <p>
     * The value determines the amount of chromosomes which participate
     * <p>
     * Default value is set to <b>0.75</b>.
     * 
     * @param crossoverRate value
     */
    public void setCrossoverRate(double crossoverRate) {
        this.crossoverRate = Math.max(0.1, Math.min(1.0, crossoverRate));
    }

    /**
     * Mutation rate, [0.1, 1].
     * <p>
     * The value determines the amount of chromosomes which participate in mutation.
     * <p>
     * Defaul value is set to <b>0.1</b>.
     */
    public double getMutationRate() {
        return mutationRate;
    }

    /**
     * Mutation rate, [0.1, 1].
     * <p>
     * The value determines the amount of chromosomes which participate in mutation.
     * <p>
     * Defaul value is set to <b>0.1</b>.
     * 
     * @param mutationRate value
     */
    public void setMutationRate(double mutationRate) {
        this.mutationRate = Math.max(0.1, Math.min(1.0, mutationRate));
    }

    /**
     * Random selection portion, [0, 0.9].
     * <p>
     * The value determines the amount of chromosomes which will be
     * randomly generated for the new population. The property controls the amount
     * of chromosomes, which are selected to a new population using
     * {@link #selectionMethod}, and amount of random
     * chromosomes added to the new population.
     * <p>
     * Default value is set to <b>0</b>.
     */
    public double getRandomSelectionPortion() {
        return randomSelectionPortion;
    }

    /**
     * Random selection portion, [0, 0.9].
     * <p>
     * The value determines the amount of chromosomes which will be
     * randomly generated for the new population. The property controls the amount
     * of chromosomes, which are selected to a new population using
     * {@link #selectionMethod}, and amount of random
     * chromosomes added to the new population.
     * <p>
     * Default value is set to <b>0</b>.
     * 
     * @param randomSelectionPortion value
     */
    public void setRandomSelectionPortion(double randomSelectionPortion) {
        this.randomSelectionPortion = Math.max(0, Math.min(0.9, randomSelectionPortion));
    }

    /**
     * Determines of auto shuffling is on or off.
     * <p>
     * The property specifies if automatic shuffling needs to be done
     * on each {@link #runEpoch()} by calling {@link #shuffle()}
     * method.
     * <p>
     * Default value is set to <see langword="false"/>.
     */
    public boolean isAutoShuffling() {
        return autoShuffling;
    }

    /**
     * Determines of auto shuffling is on or off.
     * <p>
     * The property specifies if automatic shuffling needs to be done
     * on each {@link #runEpoch()} by calling {@link #shuffle()}
     * method.
     * <p>
     * Default value is set to <see langword="false"/>.
     * 
     * @param autoShuffling value
     */
    public void setAutoShuffling(boolean autoShuffling) {
        this.autoShuffling = autoShuffling;
    }

    /**
     * Selection method to use with the population.
     * <p>
     * The property sets selection method which is used to select
     * population members for a new population - filter population after reproduction
     * was done with operators like crossover and mutations.
     */
    public ISelectionMethod getSelectionMethod() {
        return selectionMethod;
    }

    /**
     * Selection method to use with the population.
     * <p>
     * The property sets selection method which is used to select
     * population members for a new population - filter population after reproduction
     * was done with operators like crossover and mutations.
     * 
     * @param selectionMethod value
     */
    public void setSelectionMethod(ISelectionMethod selectionMethod) {
        this.selectionMethod = selectionMethod;
    }

    /**
     * Fitness function to apply to the population.
     * <P>
     * The property sets fitness function, which is used to evaluate
     * usefulness of population's chromosomes. Setting new fitness function causes recalculation
     * of fitness values for all population's members and new best member will be found.
     */
    public IFitnessFunction getFitnessFunction() {
        return fitnessFunction;
    }

    /**
     * Fitness function to apply to the population.
     * <P>
     * The property sets fitness function, which is used to evaluate
     * usefulness of population's chromosomes. Setting new fitness function causes recalculation
     * of fitness values for all population's members and new best member will be found.
     * 
     * @param fitnessFunction IFitnessFunction
     */
    public void setFitnessFunction(IFitnessFunction fitnessFunction) {
        this.fitnessFunction = fitnessFunction;
        for (IChromosome member : population) {
            member.evaluate(fitnessFunction);
        }
        findBestChromosome();
    }

    /**
     * Maximum fitness of the population.
     * <p>
     * The property keeps maximum fitness of chromosomes currently existing
     * in the population.
     * <p>
     * The property is recalculate only after {@link #selection()}
     * or {@link #migrate} was done.
     */
    public double getFitnessMax() {
        return fitnessMax;
    }

    /**
     * Summary fitness of the population.
     * <p>
     * The property keeps summary fitness of all chromosome existing in the
     * population.
     * <p>
     * The property is recalculate only after {@link #selection()}
     * or {@link #migrate} was done.
     */
    public double getFitnessSum() {
        return fitnessSum;
    }

    /**
     * Average fitness of the population.
     * <p>
     * The property keeps average fitness of all chromosome existing in the
     * population.
     * <p>
     * The property is recalculate only after {@link #selection()}
     * or {@link #migrate} was done.
     */
    public double getFitnessAvg() {
        return fitnessAvg;
    }

    /**
     * Best chromosome of the population.
     * <p>
     * The property keeps the best chromosome existing in the population
     * or null if all chromosomes have 0 fitness.
     */
    public IChromosome getBestChromosome() {
        return bestChromosome;
    }

    /**
     * Size of the population.
     * <p>
     * The property keeps initial (minimal) size of population.
     * Population always returns to this size after selection operator was applied,
     * which happens after {@link #selection} or {@link #runEpoch} methods
     * call.
     */
    public int getSize() {
        return size;
    }
 
    /**
     * Get chromosome with specified index.
     * <p>
     * Allows to access individuals of the population.
     * 
     * @param index Chromosome's index to retrieve.
     */
    public IChromosome getPopulation(int index) {
        return this.population.get(index);
    }
    
}
