package com.ontariotechu.sofe3980U;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class AppTest {
    @Test
    public void calculatesMetricsAcrossEveryRow() {
        double[] metrics = App.calculateMetrics(Arrays.asList(
                new String[] {"1", "0"},
                new String[] {"3", "1"},
                new String[] {"5", "5"}));

        assertArrayEquals(new double[] {5.0 / 3.0, 1.0, (1.0 + 2.0 / 3.0) / 3.0}, metrics, 1e-6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsEmptyInput() {
        App.calculateMetrics(Collections.emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsMalformedRows() {
        App.calculateMetrics(Collections.singletonList(new String[] {"1"}));
    }
}
