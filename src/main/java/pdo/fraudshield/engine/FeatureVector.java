package pdo.fraudshield.engine;

import pdo.fraudshield.entity.Transaction;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Arrays;

@Data
public class FeatureVector {

    private final double[] features;

    private static final int AMOUNT_NORMALIZED = 0;
    private static final int HOUR_SIN = 1;
    private static final int HOUR_COS = 2;
    private static final int DEVICE_RISK = 3;
    private static final int LOCATION_RISK = 4;
    private static final int PAYMENT_METHOD_RISK = 5;

    public FeatureVector(Transaction transaction) {
        this.features = new double[6];
        extractFeatures(transaction);
    }

    public FeatureVector(double[] features) {
        this.features = Arrays.copyOf(features, features.length);
    }

    private void extractFeatures(Transaction transaction) {
        features[AMOUNT_NORMALIZED] = sigmoidNormalizationAmount(transaction.getAmount());

        double hourRadians = (transaction.getTimestamp().getHour() * Math.PI) / 12;
        features[HOUR_SIN] = Math.sin(hourRadians);
        features[HOUR_COS] = Math.cos(hourRadians);

        features[DEVICE_RISK] = calculateDeviceRisk(transaction.getDeviceId());

        features[LOCATION_RISK] = calculateLocationRisk(transaction.getLocation());

        features[PAYMENT_METHOD_RISK] = calculatePaymentMethodRisk(transaction.getPaymentMethod());
    }

    private double sigmoidNormalizationAmount(BigDecimal amount) {
        double amountValue = amount.doubleValue();
        return 1.0 / (1.0 + Math.exp(-amountValue / 1000.0));
    }

    private double calculateDeviceRisk(String deviceId) {
        if (deviceId == null) return 0.5;
        if (deviceId.startsWith("new-")) return 0.9;
        if (deviceId.startsWith("trusted-")) return 0.1;
        return 0.3;
    }

    private double calculateLocationRisk(String location) {
        if (location == null) return 0.5;
        if (location.contains("HIGH_RISK_")) return 0.8;
        if (location.contains("LOW_RISK_")) return 0.2;
        return 0.4;
    }

    private double calculatePaymentMethodRisk(Transaction.PaymentMethod method) {
        return switch (method) {
            case PIX -> 0.7;
            case CREDIT_CARD -> 0.3;
            case DEBIT_CARD -> 0.4;
            case TRANSFER -> 0.6;
        };
    }

    // Vector operations - core of linear algebra
    public double dotProduct(FeatureVector other) {
        double sum = 0;
        for (int i = 0; i < features.length; i++) {
            sum += this.features[i] * other.features[i];
        }
        return sum;
    }

    public double magnitude() {
        double sum = 0;
        for (double feature : features) {
            sum += feature * feature;
        }
        return Math.sqrt(sum);
    }

    public double cosineSimilarity(FeatureVector other) {
        double dotProduct = this.dotProduct(other);
        double magnitudeProduct = this.magnitude() * other.magnitude();
        return magnitudeProduct == 0 ? 0 : dotProduct / magnitudeProduct;
    }

    public double euclideanDistance(FeatureVector other) {
        double sum = 0;
        for (int i = 0; i < features.length; i++) {
            double diff = this.features[i] - other.features[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
}