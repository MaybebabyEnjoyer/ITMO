package ru.covariance.dc21sample;

import org.apache.commons.math3.analysis.function.Atan2;
import org.apache.commons.math3.analysis.function.Tan;

public final class Math {
    public static double Atan(double x) {
        return new Atan2().value(x, 1);
    }

    public static double Tan(double x) {
        return new Tan().value(x);
    }
}
