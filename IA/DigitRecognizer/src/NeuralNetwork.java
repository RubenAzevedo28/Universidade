import java.util.*;

public class NeuralNetwork {

    private Perceptron neuron1;
    private Perceptron neuron2;

    private double bestMSE = Double.MAX_VALUE;
    private double[] bestWeightsNeuron1 = null;
    private double[] bestWeightsNeuron2 = null;

    public NeuralNetwork(double[] neuron1Weights, double[] neuron2Weights) {
        this.neuron1 = new Perceptron(neuron1Weights);
        this.neuron2 = new Perceptron(neuron2Weights);
    }

    public double computeOutput(double[] input) {
        double hiddenOutput = neuron1.computeOutput(input);
        return neuron2.computeOutput(hiddenOutput);
    }

    public void train(double[][] trainingData, double[] trainingLabels, double[][] testingData, double[] testingLabels, int iterations, double learningRate) {
        for (int epoch = 0; epoch < iterations; epoch++) {
            double trainingMSE = trainEpoch(trainingData, trainingLabels, learningRate);
            double testingMSE = computeMSE(testingData, testingLabels);

            if (testingMSE < bestMSE) {
                bestMSE = testingMSE;
                bestWeightsNeuron1 = Arrays.copyOf(neuron1.getWeights(), neuron1.getWeights().length);
                bestWeightsNeuron2 = Arrays.copyOf(neuron2.getWeights(), neuron2.getWeights().length);
            }

            // Log training MSE and weights
            System.out.printf("Epoch %d - Training MSE: %.8f - Test MSE: %.8f%n", epoch + 1, trainingMSE, testingMSE);
            System.out.println("Neuron 1 Weights: " + Arrays.toString(bestWeightsNeuron1));
            System.out.println("Neuron 2 Weights: " + Arrays.toString(bestWeightsNeuron2));
        }
    }

    private double trainEpoch(double[][] trainingData, double[] trainingLabels, double learningRate) {
        double mse = 0;
        for (int i = 0; i < trainingData.length; i++) {
            double[] input = trainingData[i];
            double expected = trainingLabels[i];

            double hiddenOutput = neuron1.computeOutput(input);
            double output = neuron2.computeOutput(hiddenOutput);

            double error = expected - output;
            mse += error * error;

            double delta2 = error * output * (1 - output);
            double[] gradientsNeuron2 = {delta2, delta2 * hiddenOutput};

            double delta1 = delta2 * neuron2.getWeights()[1] * hiddenOutput * (1 - hiddenOutput);
            double[] gradientsNeuron1 = new double[input.length + 1];
            gradientsNeuron1[0] = delta1;
            for (int j = 0; j < input.length; j++) {
                gradientsNeuron1[j + 1] = delta1 * input[j];
            }

            neuron2.updateWeights(gradientsNeuron2, learningRate);
            neuron1.updateWeights(gradientsNeuron1, learningRate);
        }
        return mse / trainingData.length;
    }

    public double computeMSE(double[][] data, double[] labels) {
        double mse = 0;
        for (int i = 0; i < data.length; i++) {
            double[] input = data[i];
            double expected = labels[i];

            double output = computeOutput(input);
            mse += Math.pow(expected - output, 2);
        }
        return mse / data.length;
    }

    public void test(double[][] testData, double[] testLabels) {
        int correct = 0;
        for (int i = 0; i < testData.length; i++) {
            double output = computeOutput(testData[i]) >= 0.5 ? 1 : 0;
            if (output == testLabels[i]) correct++;
        }
        System.out.println("Accuracy: " + (correct / (double) testData.length) * 100);
    }
}
