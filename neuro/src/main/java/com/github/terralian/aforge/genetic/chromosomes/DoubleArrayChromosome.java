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
 * Double数组染色体.
 * <p>
 * Double数组染色体表示double类型值的数组，数组长度在[2, 65536]之间.
 * <p>
 * 关于已实现的变异和交叉运算的信息，请参阅{@link #mutate}和{@link #crossover}方法文档
 */
public class DoubleArrayChromosome extends ChromosomeBase {
    /**
     * 染色体生成器.
     * <p>
     * 该随机数生成器用于在{@link #generate()}调用时 初始化染色体的基因.
     */
    protected IRandomNumberGenerator chromosomeGenerator;
    
    /**
     * 突变乘数生成器.
     * <p>
     * 该随机数生成器用于生成随机突变乘数，用于在突变时乘于染色体的基因.
     */
    protected IRandomNumberGenerator mutationMultiplierGenerator;

    /**
     * 突变加数生成器.
     * <p>
     * 该随机数生成器用于生成随机加数，用于在突变时与染色体基因相加.
     */
    protected IRandomNumberGenerator mutationAdditionGenerator;

    /**
     * 用于交叉和突变点选择的随机数生成器.
     * <p>
     * 该随机数生成器用于选择交叉和突变点.
     */
    protected static ThreadLocalRandom rand = ThreadLocalRandom.current();

    /**
     * 染色体最大长度.
     * <p>
     * 染色体表示的数组的最大长度.
     */
    public final int maxLength = 65536;

    /**
     * 当前染色体数组的元素长度.
     */
    private int length;

    /**
     * 染色体的值.
     */
    protected double[] val = null;

    // 控制交叉和突变类型的平衡器
    private double mutationBalancer = 0.5;
    private double crossoverBalancer = 0.5;

    /**
     * 初始化{@link DoubleArrayChromosome}类的新的实例.
     * <p>
     * 该构造函数通过调用 {@link #generate()}方法初始化生成新的随机染色体.
     * 
     * @param chromosomeGenerator 染色体生成器 - 随机数生成器，用于初始化染色体的基因，在{@link #generate}方法进行调用，类构造器也会调用.
     * @param mutationMultiplierGenerator 突变乘数生成器 - 随机数生成器, 用于生成随机突变乘数，用于在突变时乘于染色体的基因.
     * @param mutationAdditionGenerator 突变加数生成器 - 随机数生成器, 用于生成随机加数，用于在突变时与染色体基因相加.
     * @param length 染色体表示的数组的长度.
     */
    public DoubleArrayChromosome(IRandomNumberGenerator chromosomeGenerator, IRandomNumberGenerator mutationMultiplierGenerator,
            IRandomNumberGenerator mutationAdditionGenerator, int length) {

        // 保存参数
        this.chromosomeGenerator = chromosomeGenerator;
        this.mutationMultiplierGenerator = mutationMultiplierGenerator;
        this.mutationAdditionGenerator = mutationAdditionGenerator;
        this.length = Math.max(2, Math.min(maxLength, length));;

        // 分配数组空间
        val = new double[length];

        // 生成随机染色体
        generate();
    }

    /**
     * 初始化{@link DoubleArrayChromosome}类的新的实例.
     * <p>
     * 该构造器使用指定值初始化新的染色体.
     * 
     * @param chromosomeGenerator 染色体生成器 - 随机数生成器，用于初始化染色体的基因，在{@link #generate}方法进行调用，类构造器也会调用.
     * @param mutationMultiplierGenerator 突变乘数生成器 - 随机数生成器, 用于生成随机突变乘数，用于在突变时乘于染色体的基因.
     * @param mutationAdditionGenerator 突变加数生成器 - 随机数生成器, 用于生成随机加数，用于在突变时与染色体基因相加.
     * @param values 用于初始化染色体的值.
     * @throws IndexOutOfBoundsException 无效的数组长度.
     */
    public DoubleArrayChromosome(IRandomNumberGenerator chromosomeGenerator, IRandomNumberGenerator mutationMultiplierGenerator,
            IRandomNumberGenerator mutationAdditionGenerator, double[] values) {
        if ((values.length < 2) || (values.length > maxLength))
            throw new IndexOutOfBoundsException("Invalid length of values array.");

        // 保存参数
        this.chromosomeGenerator = chromosomeGenerator;
        this.mutationMultiplierGenerator = mutationMultiplierGenerator;
        this.mutationAdditionGenerator = mutationAdditionGenerator;
        this.length = values.length;

        // 复制指定值
        val = (double[]) values.clone();
    }

    /**
     * 初始化{@link DoubleArrayChromosome}类的新的实例.
     * <p>
     * 这是一个复制构造器，用于创建一个指定染色体的精确复制.
     * 
     * @param source 需要复制的来源染色体.
     */
    public DoubleArrayChromosome(DoubleArrayChromosome source) {
        this.chromosomeGenerator = source.chromosomeGenerator;
        this.mutationMultiplierGenerator = source.mutationMultiplierGenerator;
        this.mutationAdditionGenerator = source.mutationAdditionGenerator;
        this.length = source.length;
        this.fitness = source.fitness;
        this.mutationBalancer = source.mutationBalancer;
        this.crossoverBalancer = source.crossoverBalancer;

        // 复制基因
        val = (double[]) source.val.clone();
    }

    /**
     * 获取表示染色体的字符串.
     * 
     * @return 返回表示染色体的字符串.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // 追加第一个基因
        sb.append(val[0]);
        // 追加其余基因
        for (int i = 1; i < length; i++) {
            sb.append(' ');
            sb.append(val[i]);
        }

        return sb.toString();
    }

    /**
     * 生成随机染色体值.
     * <p>
     * 用随机数生成器重新生成染色体值.
     */
    @Override
    public void generate() {
        for (int i = 0; i < length; i++) {
            // 生成下一个随机数
            val[i] = chromosomeGenerator.next();
        }
    }

    /**
     * 用相同的参数创建一个新的随机染色体（工厂方法）.
     * <p>
     * 该方法创建相同类型的染色体，但是随机初始化. 该方法作为工厂方法，用于某些处理染色体接口，但是不管指定染色体类型的类.
     */
    @Override
    public IChromosome createNew() {
        return new DoubleArrayChromosome(chromosomeGenerator, mutationMultiplierGenerator, mutationAdditionGenerator, length);
    }

    /**
     * 克隆染色体.
     * <p>
     * 该方法返回一个精确的染色体克隆.
     * 
     * @return 返回该染色体的克隆.
     */
    @Override
    public IChromosome clone() {
        return new DoubleArrayChromosome(this);
    }

    /**
     * 突变操作.
     * <p>
     * 该方法执行染色体突变，将染色体基因加上随机数，或者乘于随机数. 这些随机数通过{@link #mutationMultiplierGenerator} 和 {@link #mutationAdditionGenerator} 生成.
     * 
     * <p>
     * 突变的确切类型每次都会进行选择，并且取决于{@link #mutationBalancer}. 在突变完成前，一个随机数生成在[0, 1]区间 - 若该值小于{@link #mutationBalancer}则使用乘法，否则使用加法
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
     * 交叉操作.
     * 
     * <p>
     * 该方法在两条染色体间执行交叉，随机选择交叉的确切类型，取决于{@link #crossoverBalancer}. 在突变完成前，一个随机数生成在[0, 1]区间 ，若该值小于{@link #crossoverBalancer}则使用乘法，否则使用加法
     * <p>
     * <b>第一种交叉类型（first crossover type）</b> 基于基因的交换位置(数组元素) 这些染色体间的交叉称为单点交叉. 随机选择一个交叉点，染色体交换在该点交换基因.
     * <p>
     * <b>第二种交叉（second crossover type）</b>旨在生成一个孩子，其基因值位于父母的基因之间，另一个孩子，其基因在父母基因范围之外.
     * <p> 
     * 举个例子，两个基因为1.0和3.0（当然染色体含有更多基因，这里为了简单）. 首先我们随机选择[0,1]范围内的一个因子，这里取0.4. 
     * 接着，对于每对基因（我们有一对），我们计算其差值，在该例子为2.0. 在该结果中，我们会有两个孩子，一个在父母基因之间，一个在之外.
     * 我们可能有孩子为1.8和3.8，或者可能为0.2和2.2.正如我们所看到的，我们增加/减少（随机选择）* 差异因子，这让我们探索了基因值之内与之外的情况，随机选择的因子应用于所有参与交叉的染色体基因.
     * <p>
     * <b>译注：</b> 该例子翻译困难，代码基本为ab两个基因， c =（a - b） * 因子， a = a - c, b = b + c， 通过这个操作将基因值修改到两者基因之内或者之外.
     * 但是，代码和例子不同，实际要么结果都在内部，要么都在外部，且因子也有可能取负数([-1, 1])， 不知道是不是版本关系，不过影响不大.
     */
    @Override
    public void crossover(IChromosome pair) {
        DoubleArrayChromosome p = (DoubleArrayChromosome) pair;

        // 检查配对是否正确
        if ((p != null) && (p.length == length)) {
            if (rand.nextDouble() < crossoverBalancer) {
                // 交叉点
                int crossOverPoint = rand.nextInt(length - 1) + 1;
                // 待杂交染色体的长度
                int crossOverLength = length - crossOverPoint;
                // 临时数组
                double[] temp = new double[crossOverLength];

                // 将第一（对）染色体复制到临时数组（染色体）
                System.arraycopy(val, crossOverPoint, temp, 0, crossOverLength);
                // 将第二（对）染色体复制到第一上
                System.arraycopy(p.val, crossOverPoint, val, crossOverPoint, crossOverLength);
                // 将临时数组复制到第二上
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
     * 染色体长度
     * <p>
     * 染色体的数组元素.
     */
    public int getLength() {
        return length;
    }

    /**
     * 染色体的值.
     * <p>
     * 染色体的当前值.
     */
    public double[] getValue() {
        return val;
    }
    
    /**
     * 控制突变类型的突变平衡器， [0, 1].
     * <p>
     * 该属性控制突变类型，使用频繁. 在每次突变前生成一个随机数，若随机数小于指定的平衡值，则使用一个突变类型，否则使用另一个.
     * 看{@link #mutate()}方法获得更多信息.
     * <p>
     * 默认值为 <b>0.5</b>.
     */
    public double getMutationBalancer() {
        return mutationBalancer;
    }

    /**
     * 控制突变类型的突变平衡器， [0, 1].
     * <p>
     * 该属性控制突变类型，使用频繁. 在每次突变前生成一个随机数，若随机数小于指定的平衡值，则使用一个突变类型，否则使用另一个.
     * 看{@link #mutate()}方法获取更多信息.
     * <p>
     * 默认值为 <b>0.5</b>.
     */
    public void setMutationBalancer(double mutationBalancer) {
        this.mutationBalancer = mutationBalancer;
    }

    /**
     * 控制交叉类型的交叉平衡器, [0, 1].
     * <p>
     * 该属性控制交叉类型，使用频繁. 在每次交叉之前，生成一个随机数，若随机数小于指定的平衡值，则使用一个交叉类型，否则使用另一个.
     * 看{@link #crossover}方法获取更多信息.
     * <p>
     * 默认值为 <b>0.5</b>.
     */
    public double getCrossoverBalancer() {
        return crossoverBalancer;
    }

    /**
     * 控制交叉类型的交叉平衡器, [0, 1].
     * <p>
     * 该属性控制交叉类型，使用频繁. 在每次交叉之前，生成一个随机数，若随机数小于指定的平衡值，则使用一个交叉类型，否则使用另一个.
     * 看{@link #crossover}方法获取更多信息.
     * <p>
     * 默认值为 <b>0.5</b>.
     */
    public void setCrossoverBalancer(double crossoverBalancer) {
        this.crossoverBalancer = crossoverBalancer;
    }
}
