package pdo.fraudshield.math;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pdo.fraudshield.domain.Transaction;
import pdo.fraudshield.repository.TransactionRepository;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SimilarityAnalyzer {

    private final TransactionRepository transactionRepository;

    public Map<String, Object> analyzeSimilarity(Transaction currentTransaction) {
        FeatureVector currentVector = new FeatureVector(currentTransaction);
        Map<String, Object> results = new HashMap<>();

        // Get recent transactions for this customer (context)
        List<Transaction> customerHistory = transactionRepository
                .findByCustomerId(currentTransaction.getCustomerId());

        if (customerHistory.isEmpty()) {
            results.put("similarityScore", 0.0);
            results.put("closestMatchDistance", 1.0);
            results.put("analysis", "INSUFFICIENT_HISTORY");
            return results;
        }

        List<FeatureVector> historyVectors = customerHistory.stream()
                .map(FeatureVector::new)
                .toList();

        double averageSimilarity = calculateAverageSimilarity(currentVector, historyVectors);
        double closestDistance = findClosestDistance(currentVector, historyVectors);
        double anomalyScore = calculateAnomalyScore(currentVector, historyVectors);

        results.put("similarityScore", averageSimilarity);
        results.put("closestMatchDistance", closestDistance);
        results.put("anomalyScore", anomalyScore);
        results.put("comparisonCount", historyVectors.size());
        results.put("analysis", interpretAnomalyScore(anomalyScore));

        return results;
    }

    private double calculateAverageSimilarity(FeatureVector current, List<FeatureVector> history) {
        return history.stream()
                .mapToDouble(current::cosineSimilarity)
                .average()
                .orElse(0.0);
    }

    private double findClosestDistance(FeatureVector current, List<FeatureVector> history) {
        return history.stream()
                .mapToDouble(current::euclideanDistance)
                .min()
                .orElse(1.0);
    }

    private double calculateAnomalyScore(FeatureVector current, List<FeatureVector> history) {
        double[] centroid = calculateCentroid(history);
        FeatureVector centroidVector = createVectorFromArray(centroid);
        return current.euclideanDistance(centroidVector);
    }

    private double[] calculateCentroid(List<FeatureVector> vectors) {
        if (vectors.isEmpty()) return new double[6];

        double[] centroid = new double[6];
        for (FeatureVector vector : vectors) {
            for (int i = 0; i < centroid.length; i++) {
                centroid[i] += vector.getFeatures()[i];
            }
        }

        for (int i = 0; i < centroid.length; i++) {
            centroid[i] /= vectors.size();
        }

        return centroid;
    }

    private FeatureVector createVectorFromArray(double[] features) {
        return new FeatureVector(features);
    }

    private String interpretAnomalyScore(double score) {
        if (score < 0.3) return "TYPICAL_BEHAVIOR";
        if (score < 0.6) return "SLIGHT_ANOMALY";
        if (score < 0.8) return "MODERATE_ANOMALY";
        return "SEVERE_ANOMALY";
    }
}