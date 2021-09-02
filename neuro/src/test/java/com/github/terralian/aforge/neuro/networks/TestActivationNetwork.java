package com.github.terralian.aforge.neuro.networks;

import com.github.terralian.aforge.neuro.activation.BipolarSigmoidFunction;

public class TestActivationNetwork {

    private int inputNode = 23;
    private int outputNode = 1;
    private int hiddenNode = 14;
    private double sigmoidAlphaValue = 2.0;

    public void test(double[] input, String filename) {
        ActivationNetwork network = new ActivationNetwork(new BipolarSigmoidFunction(sigmoidAlphaValue), inputNode,
                hiddenNode, outputNode);
        network.compute(input);
    }
}
