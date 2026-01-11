public class Perceptron {
    private double[] weights;

    public Perceptron(double[] initialWeights) {
        this.weights = initialWeights;
    }

    private double sigmoid(double z) {
        return 1 / (1 + Math.exp(-z));
    }

    public double computeOutput(double... inputs) {
        double z = weights[0];
        for (int i = 0; i < inputs.length; i++) {
            z += weights[i + 1] * inputs[i];
        }
        return sigmoid(z);
    }

    public void updateWeights(double[] gradients, double learningRate) {
        for (int i = 0; i < weights.length; i++) {
            weights[i] += learningRate * gradients[i];
        }
    }

    public double[] getWeights() {
        return weights;
    }
}
