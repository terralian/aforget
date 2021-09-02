// AForge Neural Net Library
// AForge.NET framework
// http://www.aforgenet.com/framework/
//
// Copyright � AForge.NET, 2007-2012
// contacts@aforgenet.com
//
package com.github.terralian.aforge.neuro.networks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import com.github.terralian.aforge.neuro.layers.Layer;

/**
 * Base neural network class.
 * <p>
 * This is a base neural netwok class, which represents collection of neuron's layers.
 */
public abstract class Network implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Network's inputs count.
     */
    protected int inputsCount;

    /**
     * Network's layers count.
     */
    protected int layersCount;

    /**
     * Network's layers.
     */
    protected Layer[] layers;

    /**
     * Network's output vector.
     */
    protected double[] output;

    /**
     * Initializes a new instance of the {@link Network} class.
     * <p>
     * Protected constructor, which initializes {@link #inputsCount}
     * 
     * @param inputsCount Network's inputs count.
     * @param layersCount Network's layers count.
     */
    protected Network(int inputsCount, int layersCount) {
        this.inputsCount = Math.max(1, inputsCount);
        this.layersCount = Math.max(1, layersCount);
        // create collection of layers
        this.layers = new Layer[this.layersCount];
    }

    /**
     * Compute output vector of the network.
     * <p>
     * The actual network's output vecor is determined by layers,
     * which comprise the layer - represents an output vector of the last layer
     * of the network. The output vector is also stored in {@link #output} property.
     * <p>
     * The method may be called safely from multiple threads to compute network's
     * output value for the specified input values. However, the value of
     * {@link #output} property in multi-threaded environment is not predictable,
     * since it may hold network's output computed from any of the caller threads. Multi-threaded
     * access to the method is useful in those cases when it is required to improve performance
     * by utilizing several threads and the computation is based on the immediate return value
     * of the method, but not on network's output property.
     * 
     * @param input Input vector.
     * @return Returns network's output vector.
     */
    public double[] compute(double[] input) {
        // local variable to avoid mutlithread conflicts
        double[] output = input;

        // compute each layer
        for (int i = 0; i < layers.length; i++) {
            output = layers[i].compute(output);
        }

        // assign output property as well (works correctly for single threaded usage)
        this.output = output;

        return output;
    }

    /**
     * Save network to specified file.
     * <p>
     * The neural network is saved using .NET serialization (binary formatter is used).
     * 
     * @param fileName File name to save network into.
     * @throws IOException if an I/O error occurs while writing stream header
     * @throws FileNotFoundException if the file exists but is a directoryrather than a regular file, does not exist but cannotbe created,
     *         or cannot be opened for any other reason
     */
    public void save(String fileName) throws FileNotFoundException, IOException {
        try (FileOutputStream fo = new FileOutputStream(new File(fileName))) {
            save(fo);
        }
    }

    /**
     * Save network to specified file.
     * 
     * @param stream Stream to save network into.
     * @throws IOException if an I/O error occurs while writing stream header
     */
    public void save(OutputStream stream) throws IOException {
        try (ObjectOutputStream oo = new ObjectOutputStream(stream)) {
            oo.writeObject(this);
        }
    }

    /**
     * Load network from specified file.
     * 
     * @param fileName File name to load network from.
     * @return Returns instance of {@link Network} class with all properties initialized from file.
     * @throws IOException if an I/O error occurs while reading stream header
     * @throws FileNotFoundException if the file does not exist,is a directory rather than a regular file,or for some other reason cannot be
     *         opened forreading.
     * @throws ClassNotFoundException Class of a serialized object cannot befound.
     */
    public static Network load(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        try(FileInputStream fi = new FileInputStream(new File(fileName))) {
            return load(fi);
        }
    }

    /**
     * Load network from specified file.
     * 
     * @param stream Stream to load network from.
     * @return Returns instance of {@link Network} class with all properties initialized from file.
     * @throws IOException if an I/O error occurs while reading stream header
     * @throws ClassNotFoundException Class of a serialized object cannot befound.
     */
    public static Network load(InputStream stream) throws IOException, ClassNotFoundException {
        try (ObjectInputStream oi = new ObjectInputStream(stream)) {
            return (Network) oi.readObject();
        }
    }

    /**
     * Randomize layers of the network.
     * <p>
     * Randomizes network's layers by calling {@link Layer#randomize()} method of each layer.
     */
    public void randomize() {
        for (Layer layer : layers) {
            layer.randomize();
        }
    }

    /**
     * Network's inputs count.
     */
    public int getInputsCount() {
        return inputsCount;
    }

    /**
     * Network's layers.
     */
    public Layer[] getLayers() {
        return layers;
    }

    /**
     * Network's output vector.
     * <p>
     * The calculation way of network's output vector is determined by
     * layers, which comprise the network.
     * <p>
     * The property is not initialized (equals to null) until
     * {@link #compute} method is called.
     */
    public double[] getOutput() {
        return output;
    }
}
