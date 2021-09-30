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
     * @return <b>True</b> 表示两个区间的最大值及最小值相等.
     */
    public static boolean equals(Range range1, Range range2) {
        return ((range1.min == range2.min) && (range1.max == range2.max));
    }
    
    /**
     * 检查该实例{@link Range}是否与指定实例相等.
     * <p>
     * 若参数非{@link Range}，返回false
     * 
     * @param obj 指定的区间.
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Range) ? (equals(this, (Range) obj)) : false;
    }

    /**
     * 检查两个区间是否不相等（最小值，最大值存在至少一个不同）.
     * 
     * @param range1 第一个要检查的区间.
     * @param range2 第二个要检查的区间.
     * @return <b>True</b> 表示两个区间的最大值及最小值不相等.
     */
    public static boolean notEquals(Range range1, Range range2) {
        return ((range1.min != range2.min) || (range1.max != range2.max));
    }

    /**
     * 区间长度 (最大值与最小值的差).
     */
    public float length() {
        return max - min;
    }

    /**
     * 获取区间最小值.
     */
    public float getMin() {
        return min;
    }

    /**
     * 设置区间最小值
     */
    public void setMin(float min) {
        this.min = min;
    }

    /**
     * 获取区间最大值
     */
    public float getMax() {
        return max;
    }

    /**
     * 设置区间最大值
     */
    public void setMax(float max) {
        this.max = max;
    }
}
