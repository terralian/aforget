// AForge Core Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2007-2011
// contacts@aforgenet.com
//
package com.github.terralian.aforge.core;

/**
 * Represents a range with minimum and maximum values, which are single precision numbers (floats).
 * <p>
 * The class represents a single precision range with inclusive limits - both minimum and maximum values of the range are included into it.
 * Mathematical notation of such range is <b>[min, max]</b>.
 * <p>
 * Sample usage:
 * <pre>
 * // create [0.25, 1.5] range
 * Range range1 = new Range(0.25f, 1.5f);
 * // create [1.00, 2.25] range
 * Range range2 = new Range(1.00f, 2.25f);
 * // check if values is inside of the first range
 * if (range1.IsInside(0.75f)) {
 *     // ...
 * }
 * // check if the second range is inside of the first range
 * if (range1.IsInside(range2)) {
 *     // ...
 * }
 * // check if two ranges overlap
 * if (range1.IsOverlapping(range2)) {
 *     // ...
 * }
 * </pre>
 */
public class Range {
    // Minimum value of the range.
    private float min;
    // Maximum value of the range.
    private float max;

    /**
     * Initializes a new instance of the {@link Range} structure.
     * 
     * @param min Minimum value of the range.
     * @param max Maximum value of the range.
     */
    public Range(float min, float max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Check if the specified value is inside of the range.
     * 
     * @param x Value to check.
     * @return <b>True</b> if the specified value is inside of the range or <b>false</b> otherwise.
     */
    public boolean isInside(float x) {
        return ((x >= min) && (x <= max));
    }

    /**
     * Check if the specified range is inside of the range.
     * 
     * @param range Range to check.
     * @return <b>True</b> if the specified value is inside of the range or <b>false</b> otherwise.
     */
    public boolean isInside(Range range) {
        return ((isInside(range.min)) && (isInside(range.max)));
    }

    /**
     * Check if the specified range overlaps with the range.
     * 
     * @param range Range to check for overlapping.
     * @return <b>True</b> if the specified value is inside of the range or <b>false</b> otherwise.
     */
    public boolean isOverlapping(Range range) {
        return ((isInside(range.min)) || (isInside(range.max)) || (range.isInside(min)) || (range.isInside(max)));
    }
    
    // ToIntRange

    /**
     * Equality operator - checks if two ranges have equal min/max values.
     * 
     * @param range1 First range to check.
     * @param range2 Second range to check.
     * @return <b>True</b> if min/max values of specified ranges are equal.
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
