package com.github.terralian.aforge.neuro.learning;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.junit.Test;

import com.github.terralian.aforge.neuro.activation.SigmoidFunction;
import com.github.terralian.aforge.neuro.networks.ActivationNetwork;

public class TestBackPropagationLearning {

    /**
     * Test BackPropagationLearning sample usage(training network to calculate XOR function)
     */
    @Test
    public void test() {
        // initialize input and output values
        double[][] input = {new double[] {0, 0}, new double[] {0, 1}, new double[] {1, 0}, new double[] {1, 1}};
        double[][] output = {new double[] {0}, new double[] {1}, new double[] {1}, new double[] {0}};
        // create neural network
        ActivationNetwork network = new ActivationNetwork(new SigmoidFunction(2), 2, 2, 1);
        // create teacher
        BackPropagationLearning teacher = new BackPropagationLearning(network);

        // iterations
        int iteration = 1;
        double learningErrorLimit = 0.1;

        boolean needToStop = false;
        List<Double> errosList = new ArrayList<>();
        // loop
        while (!needToStop) {
            // run epoch of learning procedure
            double error = teacher.runEpoch(input, output);
            // check error value to see if we need to stop
            errosList.add(error);
            System.out.println(MessageFormat.format("{0}. {1}", iteration, error));

            iteration++;

            if (error <= learningErrorLimit)
                break;
        }

        System.out.println("================= End Teaching ============");

        Function<double[], Integer> resultPrinter = (a) -> a[0] > 0.5 ? 1 : 0;
        System.out.println("[0, 0]: " + resultPrinter.apply(network.compute(new double[] {0, 0})));
        System.out.println("[0, 1]: " + resultPrinter.apply(network.compute(new double[] {0, 1})));
        System.out.println("[1, 0]: " + resultPrinter.apply(network.compute(new double[] {1, 0})));
        System.out.println("[1, 1]: " + resultPrinter.apply(network.compute(new double[] {1, 1})));
    }
}
