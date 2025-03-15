package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

/**
 * Evaluate Multiclass Classification.
 *
 * This program reads a CSV file (model.csv), computes the Cross Entropy (CE)
 * and the confusion matrix for a 5-class classification problem, and prints the results.
 */
public class App { // Changed class name to App

    public static void main(String[] args) {
        String filePath = "model.csv";
        List<String[]> data = readCSV(filePath);
        if (data == null) {
            System.out.println("Error reading " + filePath);
            return;
        }
        
        MulticlassMetrics metrics = computeMulticlassMetrics(data, 5);
        System.out.printf("CE = %.7f%n", metrics.crossEntropy);
        System.out.println("Confusion matrix");
        System.out.print("\t\t");
        for (int j = 1; j <= 5; j++) {
            System.out.print("y=" + j + "\t");
        }
        System.out.println();
        for (int i = 0; i < 5; i++) {
            System.out.print("Predicted y=" + (i+1) + "\t");
            for (int j = 0; j < 5; j++) {
                System.out.print(metrics.confusionMatrix[i][j] + "\t");
            }
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
     * Computes the Cross Entropy (CE) and confusion matrix for the given CSV data.
     * @param data CSV data where the first column is the actual class (1 to m) and the remaining m columns are the predicted probabilities.
     * @param m The number of classes.
     */
    private static MulticlassMetrics computeMulticlassMetrics(List<String[]> data, int m) {
        int n = data.size();
        double ceSum = 0.0;
        int[][] confusionMatrix = new int[m][m];
        
        // Iterate over each row
        for (String[] row : data) {
            // Parse actual class (assuming classes are 1-indexed)
            int actualClass = Integer.parseInt(row[0]);
            
            // Find the predicted class by taking the index of the maximum predicted probability
            int predictedClass = 1;
            double maxProb = Double.parseDouble(row[1]);
            for (int j = 2; j <= m; j++) {
                double prob = Double.parseDouble(row[j]);
                if (prob > maxProb) {
                    maxProb = prob;
                    predictedClass = j;
                }
            }
            
            // Add to confusion matrix (convert 1-indexed to 0-indexed)
            confusionMatrix[predictedClass - 1][actualClass - 1]++;
            
            // Accumulate cross entropy using the predicted probability of the actual class.
            double actualProb = Double.parseDouble(row[actualClass]);
            ceSum += Math.log(actualProb + 1e-6);
        }
        
        double crossEntropy = -ceSum / n;
        return new MulticlassMetrics(crossEntropy, confusionMatrix);
    }
    
    /**
     * A helper class to store multiclass metrics.
     */
    private static class MulticlassMetrics {
        double crossEntropy;
        int[][] confusionMatrix;
        
        MulticlassMetrics(double crossEntropy, int[][] confusionMatrix) {
            this.crossEntropy = crossEntropy;
            this.confusionMatrix = confusionMatrix;
        }
    }
}
