package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

/**
 * Evaluate Single Variable Continuous Regression
 */
public class App {

    public static void main(String[] args) {
        // File paths for the CSV files
        String filePath1 = "model_1.csv";
        String filePath2 = "model_2.csv";
        String filePath3 = "model_3.csv";

        List<String[]> allData1 = null;
        List<String[]> allData2 = null;
        List<String[]> allData3 = null;

        // Read CSV file for model_1.csv
        try {
            FileReader filereader1 = new FileReader(filePath1);
            CSVReader csvReader1 = new CSVReaderBuilder(filereader1)
                                        .withSkipLines(1) // Skip header row
                                        .build();
            allData1 = csvReader1.readAll();
        } catch(Exception e) {
            System.out.println("Error reading the CSV1 file");
            return;
        }

        // Read CSV file for model_2.csv
        try {
            FileReader filereader2 = new FileReader(filePath2);
            CSVReader csvReader2 = new CSVReaderBuilder(filereader2)
                                        .withSkipLines(1)
                                        .build();
            allData2 = csvReader2.readAll();
        } catch(Exception e) {
            System.out.println("Error reading the CSV2 file");
            return;
        }

        // Read CSV file for model_3.csv
        try {
            FileReader filereader3 = new FileReader(filePath3);
            CSVReader csvReader3 = new CSVReaderBuilder(filereader3)
                                        .withSkipLines(1)
                                        .build();
            allData3 = csvReader3.readAll();
        } catch(Exception e) {
            System.out.println("Error reading the CSV3 file");
            return;
        }
        
        // Process and compute metrics for each CSV file
        computeMetrics(allData1, "model_1.csv");
        computeMetrics(allData2, "model_2.csv");
        computeMetrics(allData3, "model_3.csv");
    }

    /**
     * Helper method to compute and print the metrics.
     * Computes MSE, MAE, and MARE for the first 10 rows of the given CSV data.
     * @param data List of CSV rows where each row is an array of String values.
     * @param fileName Name of the CSV file being processed.
     */
    public static void computeMetrics(List<String[]> data, String fileName) {
        int count = 0;
        double mse = 0.0;
        double mae = 0.0;
        double mare = 0.0;
        double epsilon = 1e-6; // small constant to avoid division by zero
        
        System.out.println("First 10 rows for " + fileName + ":");
        // Loop over each row in the CSV data
        for (String[] row : data) {
            // Parse true and predicted values
            float y_true = Float.parseFloat(row[0]);
            float y_predicted = Float.parseFloat(row[1]);
            
            // Calculate error for this row
            double error = y_true - y_predicted;
            mse += error * error;
            mae += Math.abs(error);
            mare += Math.abs(error) / (Math.abs(y_true) + epsilon);
            
            // Print the first 10 rows for inspection
            if (count < 10) {
                System.out.println(y_true + "\t" + y_predicted);
            }
            count++;
            // Process only the first 10 rows as per original code
            if (count == 10) {
                break;
            }
        }
        
        // Compute metrics based on the processed rows
        double computedMSE = mse / count;
        double computedMAE = mae / count;
        double computedMARE = mare / count;
        
        System.out.println("\nMetrics for " + fileName + " computed over " + count + " rows:");
        System.out.printf("MSE  = %.4f%n", computedMSE);
        System.out.printf("MAE  = %.4f%n", computedMAE);
        System.out.printf("MARE = %.4f%n", computedMARE);
        System.out.println();
    }
}
