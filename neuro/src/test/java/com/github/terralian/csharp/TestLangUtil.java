package com.github.terralian.csharp;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestLangUtil {

    @Test
    public void testArrayClear() {
        System.out.println("One dimension (Rank=1):");
        double[] numbers1 = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        for (int i = 0; i < 9; i++) {
            System.out.print(new Double(numbers1[i]).intValue() + " ");
        }
        System.out.println();
        System.out.println();

        System.out.println("Array.Clear(numbers1, 2, 5)");
        LangUtil.arrayClear(numbers1, 2, 5);

        // Expect: 1 2 0 0 0 0 0 8 9
        for (int i = 0; i < 9; i++) {
            System.out.print(new Double(numbers1[i]).intValue() + " ");
        }
        System.out.println();
        System.out.println();
    }

    @Test
    public void testListRemoveRange() {
        List<String> myAL = new ArrayList<>();
        myAL.add("The");
        myAL.add("quick");
        myAL.add("brown");
        myAL.add("fox");
        myAL.add("jumps");
        myAL.add("the");
        myAL.add("dog");

        // Displays the ArrayList.
        System.out.println("The ArrayList initially contains the following:");
        printValues(myAL);

        // Removes three elements starting at index 4.
        LangUtil.listRemoveRange(myAL, 4, 3);

        System.out.println("After removing three elements starting at index 4:");
        // Expect: The quick brown fox
        printValues(myAL);
    }

    public static void printValues(List<String> list) {
        for (String obj : list)
            System.out.print("   " + obj);
        System.out.println();
    }
}
