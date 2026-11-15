package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

/**
 * Evaluate single-variable continuous regression models.
 */
public class App {
    private static final String[] FILES = {"model_1.csv", "model_2.csv", "model_3.csv"};

    public static void main(String[] args) {
        for (String fileName : FILES) {
            try (CSVReader reader = new CSVReaderBuilder(new FileReader(fileName))
                    .withSkipLines(1)
                    .build()) {
                List<String[]> rows = reader.readAll();
                printRows(rows, fileName);
                double[] metrics = calculateMetrics(rows);
                System.out.printf("Metrics for %s computed over %d rows:%n", fileName, rows.size());
                System.out.printf("MSE  = %.4f%n", metrics[0]);
                System.out.printf("MAE  = %.4f%n", metrics[1]);
                System.out.printf("MARE = %.4f%n%n", metrics[2]);
            } catch (IOException | IllegalArgumentException exception) {
                System.err.printf("Could not process %s: %s%n", fileName, exception.getMessage());
                return;
            }
        }
    }

    static double[] calculateMetrics(List<String[]> rows) {
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("CSV contains no data rows");
        }

        double squaredError = 0.0;
        double absoluteError = 0.0;
        double absoluteRelativeError = 0.0;
        for (int index = 0; index < rows.size(); index++) {
            String[] row = rows.get(index);
            if (row.length < 2) {
                throw new IllegalArgumentException("Row " + (index + 2) + " must contain two values");
            }

            final double actual;
            final double predicted;
            try {
                actual = Double.parseDouble(row[0].trim());
                predicted = Double.parseDouble(row[1].trim());
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Row " + (index + 2) + " contains a non-numeric value", exception);
            }
            if (!Double.isFinite(actual) || !Double.isFinite(predicted)) {
                throw new IllegalArgumentException("Row " + (index + 2) + " contains a non-finite value");
            }

            double error = actual - predicted;
            squaredError += error * error;
            absoluteError += Math.abs(error);
            absoluteRelativeError += Math.abs(error) / (Math.abs(actual) + 1e-6);
        }

        double count = rows.size();
        return new double[] {
            squaredError / count,
            absoluteError / count,
            absoluteRelativeError / count
        };
    }

    private static void printRows(List<String[]> rows, String fileName) {
        System.out.println("First 10 rows for " + fileName + ":");
        for (int index = 0; index < Math.min(10, rows.size()); index++) {
            String[] row = rows.get(index);
            if (row.length >= 2) {
                System.out.println(row[0] + "\t" + row[1]);
            }
        }
    }
}
