package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

/**
 * Evaluate Single-Variable Binary Regression.
 *
 * This program reads three CSV files (model_1.csv, model_2.csv, model_3.csv),
 * calculates the BCE, confusion matrix, Accuracy, Precision, Recall, F1 score, 
 * and AUC-ROC for each, and prints the results.
 */
public class App { // Changed class name to App

    public static void main(String[] args) {
        // File paths and labels for the three models
        String[] filePaths = {"model_1.csv", "model_2.csv", "model_3.csv"};
        String[] fileLabels = {"model_1.csv", "model_2.csv", "model_3.csv"};

        // Process each CSV file and calculate metrics
        for (int i = 0; i < filePaths.length; i++) {
            List<String[]> data = readCSV(filePaths[i]);
            if (data == null) {
                System.out.println("Error reading " + filePaths[i]);
                return;
            }
            BinaryMetrics metrics = computeBinaryMetrics(data, 0.5); // threshold = 0.5
            System.out.println("for " + fileLabels[i]);
            System.out.printf("\tBCE = %.7f%n", metrics.bce);
            System.out.println("\tConfusion matrix");
            System.out.printf("\t\t\t y=1\t y=0%n");
            System.out.printf("\tPredicted 1:\t%d\t%d%n", metrics.tp, metrics.fp);
            System.out.printf("\tPredicted 0:\t%d\t%d%n", metrics.fn, metrics.tn);
            System.out.printf("\tAccuracy = %.4f%n", metrics.accuracy);
            System.out.printf("\tPrecision = %.7f%n", metrics.precision);
            System.out.printf("\tRecall = %.8f%n", metrics.recall);
            System.out.printf("\tf1 score = %.8f%n", metrics.f1Score);
            System.out.printf("\tauc roc = %.8f%n", metrics.aucRoc);
            System.out.println();
        }
    }

    /**
     * Reads a CSV file and returns the data as a list of String arrays.
     * Skips the header row.
     */
    private static List<String[]> readCSV(String filePath) {
        try {
            FileReader filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();
            return csvReader.readAll();
        } catch (Exception e) {
            System.out.println("Error reading the CSV file: " + filePath);
            return null;
        }
    }

    /**
     * Computes binary regression metrics for the given CSV data.
     * @param data CSV data containing two columns: true label and predicted probability.
     * @param threshold Threshold to convert probability to binary prediction.
     */
    private static BinaryMetrics computeBinaryMetrics(List<String[]> data, double threshold) {
        int n = data.size();
        double bceSum = 0.0;
        double epsilon = 1e-6; // to avoid log(0)
        
        // Confusion matrix counters
        int tp = 0, tn = 0, fp = 0, fn = 0;
        
        // Arrays to store TPR and FPR for ROC computation (101 thresholds from 0 to 1)
        double[] tprArray = new double[101];
        double[] fprArray = new double[101];
        
        // First pass: calculate BCE and confusion matrix at the default threshold
        for (String[] row : data) {
            int actual = Integer.parseInt(row[0]);
            double predictedProb = Double.parseDouble(row[1]);
            // BCE component
            bceSum += actual * Math.log(predictedProb + epsilon) + 
                      (1 - actual) * Math.log(1 - predictedProb + epsilon);
            
            // Confusion matrix using the given threshold
            int predicted = predictedProb >= threshold ? 1 : 0;
            if (actual == 1 && predicted == 1) {
                tp++;
            } else if (actual == 0 && predicted == 0) {
                tn++;
            } else if (actual == 0 && predicted == 1) {
                fp++;
            } else if (actual == 1 && predicted == 0) {
                fn++;
            }
        }
        double bce = -bceSum / n;
        double accuracy = (double) (tp + tn) / n;
        double precision = tp + fp == 0 ? 0 : (double) tp / (tp + fp);
        double recall = tp + fn == 0 ? 0 : (double) tp / (tp + fn);
        double f1Score = (precision + recall) == 0 ? 0 : 2 * precision * recall / (precision + recall);
        
        // For ROC curve, we need the total number of positive and negative examples
        int totalPositives = 0, totalNegatives = 0;
        for (String[] row : data) {
            int actual = Integer.parseInt(row[0]);
            if (actual == 1) {
                totalPositives++;
            } else {
                totalNegatives++;
            }
        }
        
        // Calculate TPR and FPR at 101 thresholds (0, 0.01, ..., 1)
        for (int i = 0; i <= 100; i++) {
            double th = i / 100.0;
            int currentTP = 0, currentFP = 0;
            for (String[] row : data) {
                int actual = Integer.parseInt(row[0]);
                double predictedProb = Double.parseDouble(row[1]);
                if (predictedProb >= th) {
                    if (actual == 1) {
                        currentTP++;
                    } else {
                        currentFP++;
                    }
                }
            }
            tprArray[i] = totalPositives == 0 ? 0 : (double) currentTP / totalPositives;
            fprArray[i] = totalNegatives == 0 ? 0 : (double) currentFP / totalNegatives;
        }
        
        // Compute AUC-ROC using the trapezoidal rule
        double aucRoc = 0.0;
        for (int i = 1; i <= 100; i++) {
            double deltaFPR = Math.abs(fprArray[i] - fprArray[i - 1]);
            aucRoc += (tprArray[i - 1] + tprArray[i]) * deltaFPR / 2;
        }
        
        return new BinaryMetrics(bce, tp, tn, fp, fn, accuracy, precision, recall, f1Score, aucRoc);
    }

    /**
     * A helper class to store binary regression metric results.
     */
    private static class BinaryMetrics {
        double bce;
        int tp, tn, fp, fn;
        double accuracy, precision, recall, f1Score, aucRoc;
        
        BinaryMetrics(double bce, int tp, int tn, int fp, int fn, double accuracy,
                      double precision, double recall, double f1Score, double aucRoc) {
            this.bce = bce;
            this.tp = tp;
            this.tn = tn;
            this.fp = fp;
            this.fn = fn;
            this.accuracy = accuracy;
            this.precision = precision;
            this.recall = recall;
            this.f1Score = f1Score;
            this.aucRoc = aucRoc;
        }
    }
}
