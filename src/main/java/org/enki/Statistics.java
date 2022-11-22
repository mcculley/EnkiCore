package org.enki;

import java.util.Collection;
import java.util.List;

import static java.lang.Math.abs;

public class Statistics {

    private Statistics() {
        throw new AssertionError("static utility class is not intended to be instantiated");
    }

    public static double standardDeviation(final Collection<? extends Number> values) {
        final double mean = values.stream().mapToDouble(Number::doubleValue).average().orElseThrow();
        final double sumOfSquaresOfDistance =
                values.stream().mapToDouble(x -> Math.pow(x.doubleValue() - mean, 2)).sum();
        return Math.sqrt(sumOfSquaresOfDistance / values.size());
    }

    private static boolean close(final double a, final double b, final double epsilon) {
        return abs(a - b) <= epsilon;
    }

    static {
        assert standardDeviation(List.of(50, 50, 50, 50)) == 0;
        assert standardDeviation(List.of(2, 4, 4, 4, 5, 5, 7, 9)) == 2;
        assert close(standardDeviation(List.of(9, 2, 5, 4, 12, 7, 8, 11, 9, 3, 7, 4, 12, 5, 4, 10, 9, 6, 9, 4)),
                2.983, 0.1);
    }

}
