package pdo.fraudshield.math;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class GradientOptimizer {

    private double[] weights = {0.6, 0.4}; // [ruleWeight, similarityWeight]

    // Learning rate for gradient descent
    private final double learningRate = 0.01;

    public Map<String, Object> optimizeWeights(double ruleScore, double similarityScore, boolean wasFraud) {
        Map<String, Object> results = new HashMap<>();

        double currentPrediction = predict(ruleScore, similarityScore);

        double[] gradient = calculateGradient(ruleScore, similarityScore, currentPrediction, wasFraud);

        updateWeights(gradient);

        double optimizedPrediction = predict(ruleScore, similarityScore);

        results.put("previousWeights", Arrays.copyOf(weights, weights.length));
        results.put("currentWeights", Arrays.copyOf(weights, weights.length));
        results.put("gradient", gradient);
        results.put("previousPrediction", currentPrediction);
        results.put("optimizedPrediction", optimizedPrediction);
        results.put("errorReduction", calculateErrorReduction(currentPrediction, optimizedPrediction, wasFraud));

        log.info("Weights optimized: {} -> {}",
                Arrays.toString((long[]) results.get("previousWeights")),
                Arrays.toString((long[]) results.get("currentWeights")));

        return results;
    }

    private double predict(double ruleScore, double similarityScore) {
        // Linear combination: w1*rules + w2*similarity
        return weights[0] * ruleScore + weights[1] * similarityScore;
    }

    private double[] calculateGradient(double ruleScore, double similarityScore,
                                       double prediction, boolean wasFraud) {
        double error = prediction - (wasFraud ? 1.0 : 0.0);

        // Partial derivatives of error with respect to each weight
        double dError_dW1 = 2 * error * ruleScore;  // ∂E/∂w1
        double dError_dW2 = 2 * error * similarityScore; // ∂E/∂w2

        return new double[]{dError_dW1, dError_dW2};
    }

    private void updateWeights(double[] gradient) {
        // Gradient descent: w = w - learningRate * gradient
        for (int i = 0; i < weights.length; i++) {
            weights[i] = weights[i] - learningRate * gradient[i];
        }

        // Ensure weights sum to 1 (maintain probability interpretation)
        normalizeWeights();
    }

    private void normalizeWeights() {
        double sum = Arrays.stream(weights).sum();
        if (sum != 0) {
            for (int i = 0; i < weights.length; i++) {
                weights[i] = weights[i] / sum;
            }
        }
    }

    private double calculateErrorReduction(double oldPrediction, double newPrediction, boolean wasFraud) {
        double target = wasFraud ? 1.0 : 0.0;
        double oldError = Math.pow(oldPrediction - target, 2);
        double newError = Math.pow(newPrediction - target, 2);
        return oldError - newError; // Positive means improvement
    }

    public double[] getCurrentWeights() {
        return Arrays.copyOf(weights, weights.length);
    }
}