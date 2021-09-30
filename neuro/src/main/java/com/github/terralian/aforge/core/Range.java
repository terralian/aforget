// AForge Core Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2007-2011
// contacts@aforgenet.com
//
package com.github.terralian.aforge.core;

/**
 * 表示一个具有最小值与最大值的单精度(floats)区间.
 * <p>
 * 该类表示一个包含限制的单精度区间 - 内含最小值与最大值. 这个区间的数学符号是 <b>[min, max]</b>.
 * <p>
 * 样例:
 * 
 * <pre>
 * // 创建 [0.25, 1.5] 区间
 * Range range1 = new Range(0.25f, 1.5f);
 * // 创建 [1.00, 2.25] 区间
 * Range range2 = new Range(1.00f, 2.25f);
 * // 检查值是否在第一个区间内
 * if (range1.IsInside(0.75f)) {
 *     // ...
 * }
 * // 检查第二个区间是否在第一个区间内
 * if (range1.IsInside(range2)) {
 *     // ...
 * }
 * // 检查两个区间是否存在重叠
 * if (range1.IsOverlapping(range2)) {
 *     // ...
 * }
 * </pre>
 */
public class Range {
    // 区间最小值.
    private float min;
    // 区间最大值.
    private float max;

    /**
     * 初始化一个{@link Range}数据结构实例.
     * 
     * @param min 区间最小值.
     * @param max 区间最大值.
     */
    public Range(float min, float max) {
        this.min = min;
        this.max = max;
    }

    /**
     * 检查指定的值是否在区间内.
     * 
     * @param x 要检查的值.
     * @return <b>True</b> 表示在指定值在区间内，反之 <b>false</b>.
     */
    public boolean isInside(float x) {
        return ((x >= min) && (x <= max));
    }

    /**
     * 检查指定的区间是否在区间内.
     * 
     * @param range 要检查的区间.
     * @return <b>True</b> 表示指定的区间在区间内，反之 <b>false</b>.
     */
    public boolean isInside(Range range) {
        return ((isInside(range.min)) && (isInside(range.max)));
    }

    /**
     * 检查指定区间是否与该区间重叠（含有交集）.
     * 
     * @param range 要检查的区间.
     * @return <b>True</b> 表示指定区间与该区间重叠，反之 <b>false</b>.
     */
    public boolean isOverlapping(Range range) {
        return ((isInside(range.min)) || (isInside(range.max)) || (range.isInside(min)) || (range.isInside(max)));
    }
    
    // 下面的这个方法需要引入一个新的类，由于没用到这里先去除.
    // ToIntRange

    /**
     * 检查两个区间是否相等（存在相同的最小值，最大值）.
     * 
     * @param range1 第一个要检查的区间.
     * @param range2 第二个要检查的区间.
     * @return <b>True</b> 表示两个区间的最大值及最小值相同.
     */
    public static boolean equals(Range range1, Range range2) {
        return ((range1.min == range2.min) && (range1.max == range2.max));
    }
    
    /**
     * Check if this instance of {@link Range} equal to the specified one.
     * 
     * @param Another range to check equalty to.
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Range) ? (equals(this, (Range) obj)) : false;
    }

    /**
     * Inequality operator - checks if two ranges have different min/max values.
     * 
     * @param range1 First range to check.
     * @param range2 Second range to check.
     * @return <b>True</b> if min/max values of specified ranges are not equal.
     */
    public static boolean notEquals(Range range1, Range range2) {
        return ((range1.min != range2.min) || (range1.max != range2.max));
    }

    /**
     * Length of the range (deffirence between maximum and minimum values).
     */
    public float length() {
        return max - min;
    }

    /**
     * Minimum value of the range.
     * <p>
     * The property represents minimum value (left side limit) or the range - [<b>min</b>, max].
     */
    public float getMin() {
        return min;
    }

    /**
     * Minimum value of the range.
     * <p>
     * The property represents minimum value (left side limit) or the range - [<b>min</b>, max].
     */
    public void setMin(float min) {
        this.min = min;
    }

    /**
     * Maximum value of the range.
     * <p>
     * The property represents maximum value (right side limit) or the range - [min, <b>max</b>].
     */
    public float getMax() {
        return max;
    }

    /**
     * Maximum value of the range.
     * <p>
     * The property represents maximum value (right side limit) or the range - [min, <b>max</b>].
     */
    public void setMax(float max) {
        this.max = max;
    }
}
