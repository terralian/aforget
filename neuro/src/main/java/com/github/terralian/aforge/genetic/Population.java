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
 * 染色体的种群（Population）.
 * <p>
 * 该类表示一个种群 - 收集个体（染色体）并提供实用及公共的种群生命周期 - 借助遗传算子来扩大种群数量，
 * 借助选择算法对染色体进行选择，生成新一代的染色体. 该类可以与任意染色体{@link IChromosome}接口实例
 * 一起工作，并使用任意适应度函数(Fitness Function){@link IFitnessFunction}接口实例及使用任意
 * 选择算法{@link ISelectionMethod}接口实例.
 * 
 */
public class Population {

    private IFitnessFunction fitnessFunction;
    private ISelectionMethod selectionMethod;
    private List<IChromosome> population = new ArrayList<>();
    private int size;
    private double randomSelectionPortion = 0.0;
    private boolean autoShuffling = false;

    // 种群参数
    private double crossoverRate = 0.75;
    private double mutationRate = 0.10;

    // 随机数生成器
    private static ThreadLocalRandom rand = ThreadLocalRandom.current();

    //
    private double fitnessMax = 0;
    private double fitnessSum = 0;
    private double fitnessAvg = 0;
    private IChromosome bestChromosome = null;

    /**
     * 初始化一个种群{@link Population}实例.
     * <p>
     * 创建一个指定大小的种群. 祖先染色体将作为种群的第一个成员，并使用统一的参数创建剩下的其他成员.
     * 
     * @param size 种群大小初始值.
     * @param ancestor 用于创建种群的先祖染色体.
     * @param fitnessFunction 用于计算染色体适应值的适应度函数.
     * @param selectionMethod 用于选择新一代染色体的选择算法.
     * @throws IllegalArgumentException 指定的种群规模太小。若size小于2，则抛出该异常.
     */
    public Population(int size, IChromosome ancestor, IFitnessFunction fitnessFunction, ISelectionMethod selectionMethod) {
        if (size < 2)
            throw new IllegalArgumentException("Too small population's size was specified.");

        this.fitnessFunction = fitnessFunction;
        this.selectionMethod = selectionMethod;
        this.size = size;

        // 将祖先添加到种群
        ancestor.evaluate(fitnessFunction);
        population.add(ancestor.clone());
        // 添加更多的染色体到种群
        for (int i = 1; i < size; i++) {
            // 创建新的染色体
            IChromosome c = ancestor.createNew();
            // 计算其适应度
            c.evaluate(fitnessFunction);
            // 添加到种群
            population.add(c);
        }
    }

    /**
     * 重新生成种群.
     * <p>
     * 该方法使用随机染色体重新生成种群.
     */
    public void regenerate() {
        IChromosome ancestor = population.get(0);

        // 清空种群
        population.clear();
        // 添加染色体到种群
        for (int i = 0; i < size; i++) {
            // 创建新染色体
            IChromosome c = ancestor.createNew();
            // 计算其适应度
            c.evaluate(fitnessFunction);
            // 添加到种群
            population.add(c);
        }
    }

    /**
     * 执行种群交叉（crossover）.
     * <p>
     * 该方法遍历种群并按顺序对每两条染色体执行交叉算子. 配对的染色体总数由交叉概率（{@link #crossoverRate}）决定
     */
    public void crossover() {
        // 交叉
        for (int i = 1; i < size; i += 2) {
            // 生成下一个随机数，并判断是否需要进行交叉
            if (rand.nextDouble() <= crossoverRate) {
                // 克隆自身及祖先
                IChromosome c1 = population.get(i - 1).clone();
                IChromosome c2 = population.get(i).clone();

                // 执行交叉
                c1.crossover(c2);

                // 对后代计算适应度
                c1.evaluate(fitnessFunction);
                c2.evaluate(fitnessFunction);

                // 将后代添加到种群
                population.add(c1);
                population.add(c2);
            }
        }
    }

    /**
     * 执行种群突变.
     * <p>
     * 该方法遍历种群，对每一个染色体执行突变操作. 突变的染色体总数由突变概率（{@link #mutationRate}）决定.
     */
    public void mutate() {
        // 突变
        for (int i = 0; i < size; i++) {
            // 生成下一个随机数并判断是否进行突变
            if (rand.nextDouble() <= mutationRate) {
                // 克隆染色体
                IChromosome c = population.get(i).clone();
                // 突变
                c.mutate();
                // 对突变型计算适应度
                c.evaluate(fitnessFunction);
                // 将突变型加入到种群
                population.add(c);
            }
        }
    }

    /**
     * 执行选择.<p>
     * 该方法对当前种群进行选择操作. 使用指定的选择算法对当前种群进行选择作为新种群，并在需要时添加定义数量的随机成员.
     * 参考 {@link #randomSelectionPortion}
     */
    public void selection() {
        // 新种群中的随机染色体数量
        int randomAmount = (int) (randomSelectionPortion * size);

        // 执行选择
        selectionMethod.applySelection(population, size - randomAmount);

        // 增加随机的染色体
        if (randomAmount > 0) {
            IChromosome ancestor = population.get(0);

            for (int i = 0; i < randomAmount; i++) {
                // 创建新染色体
                IChromosome c = ancestor.createNew();
                // 计算适应度
                c.evaluate(fitnessFunction);
                // 添加到种群
                population.add(c);
            }
        }

        findBestChromosome();
    }

    /**
     * 执行一个种群时期（epoch）.
     * <p>
     * 该方法执行一个种群时期（epoch），进行交叉，突变及选择。通过调用 {@link #crossover()}, {@link #mutate()} 和
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
     * 对当前种群进行随机洗牌.
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

    // 找到种群中迄今为止最好的染色体
    private void findBestChromosome() {
        bestChromosome = population.get(0);
        fitnessMax = bestChromosome.getFitness();
        fitnessSum = fitnessMax;

        for (int i = 1; i < size; i++) {
            double fitness = population.get(i).getFitness();

            // 累加汇总值
            fitnessSum += fitness;

            // 检查最大值
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
