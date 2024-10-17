package org.murraybridgebunyips.bunyipslib.roadrunner.util;

import androidx.annotation.Nullable;

import com.acmerobotics.roadrunner.kinematics.Kinematics;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Various regression utilities.
 *
 * @since 1.0.0-pre
 */
public final class RegressionUtil {

    private RegressionUtil() {
    }

    /**
     * Numerically compute dy/dx from the given x and y values. The returned list is padded to match
     * the length of the original sequences.
     *
     * @param x x-values
     * @param y y-values
     * @return derivative values
     */
    private static List<Double> numericalDerivative(List<Double> x, List<Double> y) {
        List<Double> deriv = new ArrayList<>(x.size());
        for (int i = 1; i < x.size() - 1; i++) {
            deriv.add(
                    (y.get(i + 1) - y.get(i - 1)) /
                            (x.get(i + 1) - x.get(i - 1))
            );
        }
        // copy endpoints to pad output
        deriv.add(0, deriv.get(0));
        deriv.add(deriv.get(deriv.size() - 1));
        return deriv;
    }

    /**
     * Run regression to compute velocity and static feedforward from ramp test data.
     * <p>
     * Here's the general procedure for gathering the requisite data:
     * 1. Slowly ramp the motor power/voltage and record encoder values along the way.
     * 2. Run a linear regression on the encoder velocity vs. motor power plot to obtain a slope
     * (kV) and an optional intercept (kStatic).
     *
     * @param timeSamples     time samples
     * @param positionSamples position samples
     * @param powerSamples    power samples
     * @param fitStatic       fit kStatic
     * @param file            log file
     * @return feedforward parameter estimates and additional summary statistics
     */
    public static RampResult fitRampData(List<Double> timeSamples, List<Double> positionSamples,
                                         List<Double> powerSamples, boolean fitStatic,
                                         @Nullable File file) {
        if (file != null) {
            try (PrintWriter pw = new PrintWriter(file)) {
                pw.println("time,position,power");
                for (int i = 0; i < timeSamples.size(); i++) {
                    double time = timeSamples.get(i);
                    double pos = positionSamples.get(i);
                    double power = powerSamples.get(i);
                    pw.println(time + "," + pos + "," + power);
                }
            } catch (FileNotFoundException e) {
                // ignore
            }
        }

        List<Double> velSamples = numericalDerivative(timeSamples, positionSamples);

        SimpleRegression rampReg = new SimpleRegression(fitStatic);
        for (int i = 0; i < timeSamples.size(); i++) {
            double vel = velSamples.get(i);
            double power = powerSamples.get(i);

            rampReg.addData(vel, power);
        }

        return new RampResult(Math.abs(rampReg.getSlope()), Math.abs(rampReg.getIntercept()),
                rampReg.getRSquare());
    }

    /**
     * Run regression to compute acceleration feedforward.
     *
     * @param timeSamples     time samples
     * @param positionSamples position samples
     * @param powerSamples    power samples
     * @param rampResult      ramp result
     * @param file            log file
     * @return feedforward parameter estimates and additional summary statistics
     */
    public static AccelResult fitAccelData(List<Double> timeSamples, List<Double> positionSamples,
                                           List<Double> powerSamples, RampResult rampResult,
                                           @Nullable File file) {
        if (file != null) {
            try (PrintWriter pw = new PrintWriter(file)) {
                pw.println("time,position,power");
                for (int i = 0; i < timeSamples.size(); i++) {
                    double time = timeSamples.get(i);
                    double pos = positionSamples.get(i);
                    double power = powerSamples.get(i);
                    pw.println(time + "," + pos + "," + power);
                }
            } catch (FileNotFoundException e) {
                // ignore
            }
        }

        List<Double> velSamples = numericalDerivative(timeSamples, positionSamples);
        List<Double> accelSamples = numericalDerivative(timeSamples, velSamples);

        SimpleRegression accelReg = new SimpleRegression(false);
        for (int i = 0; i < timeSamples.size(); i++) {
            double vel = velSamples.get(i);
            double accel = accelSamples.get(i);
            double power = powerSamples.get(i);

            double powerFromVel = Kinematics.calculateMotorFeedforward(
                    vel, 0.0, rampResult.kV, 0.0, rampResult.kStatic);
            double powerFromAccel = power - powerFromVel;

            accelReg.addData(accel, powerFromAccel);
        }

        return new AccelResult(Math.abs(accelReg.getSlope()), accelReg.getRSquare());
    }

    /**
     * Feedforward parameter estimates from the ramp regression and additional summary statistics
     */
    public static class RampResult {
        /**
         * Velocity feedforward gain
         */
        public final double kV,
        /**
         * Static feedforward gain
         */
        kStatic,
        /**
         * R^2 value of the regression
         */
        rSquare;

        /**
         * @param kV      velocity feedforward gain
         * @param kStatic static feedforward gain
         * @param rSquare R^2 value of the regression
         */
        public RampResult(double kV, double kStatic, double rSquare) {
            this.kV = kV;
            this.kStatic = kStatic;
            this.rSquare = rSquare;
        }
    }

    /**
     * Feedforward parameter estimates from the ramp regression and additional summary statistics
     */
    public static class AccelResult {
        /**
         * Acceleration feedforward gain
         */
        public final double kA,
        /**
         * R^2 value of the regression
         */
        rSquare;

        /**
         * @param kA      acceleration feedforward gain
         * @param rSquare R^2 value of the regression
         */
        public AccelResult(double kA, double rSquare) {
            this.kA = kA;
            this.rSquare = rSquare;
        }
    }
}