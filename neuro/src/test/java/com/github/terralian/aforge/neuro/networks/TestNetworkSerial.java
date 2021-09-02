package com.github.terralian.aforge.neuro.networks;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import com.github.terralian.aforge.neuro.layers.Layer;
import com.github.terralian.aforge.neuro.neurons.Neuron;
import com.github.terralian.aforge.neuro.test.TestConfig;

public class TestNetworkSerial {

    public static final double[] data = {1.0, 2.0, 3.0};

    @Test
    public void test() throws FileNotFoundException, IOException, ClassNotFoundException {
        SerialNeuron[] neurons = new SerialNeuron[1];
        neurons[0] = new SerialNeuron(data);
        SerialLayer[] layers = new SerialLayer[1];
        layers[0] = new SerialLayer(neurons, data);
        SerialNetwork network = new SerialNetwork(data, layers);
        String serialFileName = TestConfig.outFileName("serial.txt");
        network.save(serialFileName);
        Network loadNetwork = Network.load(serialFileName);

        assertEquals(1, loadNetwork.getLayers().length);
        assertEquals(1, loadNetwork.getLayers()[0].getNeurons().length);
        assertEquals(3, loadNetwork.getOutput().length);
        assertEquals(3, loadNetwork.getLayers()[0].getOutput().length);
    }

    private static class SerialNetwork extends Network {
        private static final long serialVersionUID = 1L;

        protected SerialNetwork(double[] output, Layer[] layers) {
            super(output.length, layers.length);
            super.output = output;
            super.layers = layers;
        }
    }

    private static class SerialLayer extends Layer {
        private static final long serialVersionUID = 1L;

        protected SerialLayer(Neuron[] neurons, double[] output) {
            super(neurons.length, output.length);
            super.neurons = neurons;
            super.output = output;
        }
    }

    private static class SerialNeuron extends Neuron {
        private static final long serialVersionUID = 1L;

        protected SerialNeuron(double[] weights) {
            super(weights.length);
            super.weights = weights;
        }

        @Override
        public double compute(double[] input) {
            return 0;
        }
    }
}
