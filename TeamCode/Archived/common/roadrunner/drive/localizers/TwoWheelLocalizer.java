package org.murraybridgebunyips.bunyipslib.localization;

import static org.murraybridgebunyips.bunyipslib.external.units.Units.Inches;

import androidx.annotation.NonNull;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.localization.Localizer;
import com.acmerobotics.roadrunner.localization.TwoTrackingWheelLocalizer;

import org.murraybridgebunyips.bunyipslib.drive.Moveable;
import org.murraybridgebunyips.bunyipslib.external.units.Distance;
import org.murraybridgebunyips.bunyipslib.external.units.Measure;
import org.murraybridgebunyips.bunyipslib.roadrunner.util.Deadwheel;

import java.util.Arrays;
import java.util.List;

/**
 * Dual tracking wheel localizer implementation.
 *
 * @since 4.0.0
 */
/*
 * Tracking wheel localizer implementation assuming the standard configuration:
 *
 *    ^
 *    |
 *    | ( x direction)
 *    |
 *    v
 *    <----( y direction )---->

 *        (forward)
 *    /--------------\
 *    |     ____     |
 *    |     ----     |    <- Perpendicular Wheel
 *    |           || |
 *    |           || |    <- Parallel Wheel
 *    |              |
 *    |              |
 *    \--------------/
 */
public class TwoWheelLocalizer extends TwoTrackingWheelLocalizer {
    private final TwoWheelLocalizer.Coefficients coefficients;

    // Parallel/Perpendicular to the forward axis
    // Parallel wheel is parallel to the forward axis
    // Perpendicular is perpendicular to the forward axis
    private final Deadwheel parallelDeadwheel;
    private final Deadwheel perpendicularDeadwheel;
    private final double xMul;
    private final double yMul;
    private final Localizer localizer;
    private boolean usingOverflowCompensation;

    /**
     * Create a new TwoWheelLocalizer.
     *
     * @param coefficients           The coefficients to use
     * @param parallelDeadwheel      The parallel encoder
     * @param perpendicularDeadwheel The perpendicular encoder
     * @param drive                  The drivetrain which will be used to obtain heading information from
     */
    public TwoWheelLocalizer(TwoWheelLocalizer.Coefficients coefficients, Deadwheel parallelDeadwheel, Deadwheel perpendicularDeadwheel, Moveable drive) {
        this(coefficients, parallelDeadwheel, perpendicularDeadwheel, drive.getLocalizer());
    }

    /**
     * Create a new TwoWheelLocalizer.
     *
     * @param coefficients           The coefficients to use
     * @param parallelDeadwheel      The parallel encoder
     * @param perpendicularDeadwheel The perpendicular encoder
     * @param headingLocalizer       The localizer to use for heading information
     */
    public TwoWheelLocalizer(TwoWheelLocalizer.Coefficients coefficients, Deadwheel parallelDeadwheel, Deadwheel perpendicularDeadwheel, Localizer headingLocalizer) {
        super(Arrays.asList(
                new Pose2d(coefficients.PARALLEL_X, coefficients.PARALLEL_Y, 0),
                new Pose2d(coefficients.PERPENDICULAR_X, coefficients.PERPENDICULAR_Y, Math.toRadians(90))
        ));

        localizer = headingLocalizer;
        this.coefficients = coefficients;
        if (this.coefficients.USE_CORRECTED_COUNTS)
            enableOverflowCompensation();

        this.parallelDeadwheel = parallelDeadwheel;
        this.perpendicularDeadwheel = perpendicularDeadwheel;

        xMul = coefficients.X_MULTIPLIER;
        yMul = coefficients.Y_MULTIPLIER;
    }

    /**
     * Enable overflow compensation if your encoders exceed 32767 counts / second.
     *
     * @return this
     */
    public TwoWheelLocalizer enableOverflowCompensation() {
        usingOverflowCompensation = true;
        return this;
    }

    public TwoWheelLocalizer.Coefficients getCoefficients() {
        return coefficients;
    }

    /**
     * Convert encoder ticks to inches
     *
     * @param ticks The ticks to convert
     * @return The inches traveled
     */
    public double encoderTicksToInches(double ticks) {
        return coefficients.WHEEL_RADIUS * 2 * Math.PI * coefficients.GEAR_RATIO * ticks / coefficients.TICKS_PER_REV;
    }

    @Override
    public double getHeading() {
        return localizer.getPoseEstimate().getHeading();
    }

    @Override
    public Double getHeadingVelocity() {
        Pose2d velo = localizer.getPoseVelocity();
        return velo != null ? velo.getHeading() : 0.0;
    }

    @NonNull
    @Override
    public List<Double> getWheelPositions() {
        return Arrays.asList(
                encoderTicksToInches(parallelDeadwheel.getCurrentPosition()) * xMul,
                encoderTicksToInches(perpendicularDeadwheel.getCurrentPosition()) * yMul
        );
    }

    @NonNull
    @Override
    public List<Double> getWheelVelocities() {
        // If your encoder velocity can exceed 32767 counts / second (such as the REV Through Bore and other
        // high resolution encoders), enable overflow compensation with enableOverflowCompensation.
        return Arrays.asList(
                encoderTicksToInches(usingOverflowCompensation ? parallelDeadwheel.getCorrectedVelocity() : parallelDeadwheel.getRawVelocity()) * xMul,
                encoderTicksToInches(usingOverflowCompensation ? perpendicularDeadwheel.getCorrectedVelocity() : perpendicularDeadwheel.getRawVelocity()) * yMul
        );
    }

    /**
     * Coefficients for RoadRunner two wheel tracking localizer.
     * Reworked to use a builder for multiple robot configurations.
     *
     * @author Lucas Bubner, 2023
     */
    public static class Coefficients {
        /**
         * The number of ticks per revolution of the encoders
         */
        public double TICKS_PER_REV;
        /**
         * The radius of the tracking wheels in inches
         */
        public double WHEEL_RADIUS = 2;
        /**
         * Gear ratio of the tracking wheels. Calculated as {@code output (wheel) speed / input (encoder) speed}.
         */
        public double GEAR_RATIO = 1;
        /**
         * Position in the forward direction of the parallel wheels in inches
         */
        public double PARALLEL_X;
        /**
         * Position in the strafe direction of the parallel wheels in inches
         */
        public double PARALLEL_Y;
        /**
         * Position in the forward direction of the perpendicular wheels in inches
         */
        public double PERPENDICULAR_X;
        /**
         * Position in the strafe direction of the perpendicular wheels in inches
         */
        public double PERPENDICULAR_Y;
        /**
         * Multiplicative scale of the ticks from the x (forward) axis.
         */
        public double X_MULTIPLIER = 1;
        /**
         * Multiplicative scale of the ticks from the y (strafe) axis.
         */
        public double Y_MULTIPLIER = 1;
        /**
         * Whether to use corrected overflow counts if the TPS exceeds 32767.
         */
        public boolean USE_CORRECTED_COUNTS = false;

        /**
         * Utility class for building TwoWheelLocalizer.Coefficients
         */
        public static class Builder {
            private final TwoWheelLocalizer.Coefficients twoWheelTrackingCoefficients;

            /**
             * Start building.
             */
            public Builder() {
                twoWheelTrackingCoefficients = new TwoWheelLocalizer.Coefficients();
            }

            /**
             * Set the ticks per revolution of the tracking wheel encoder.
             *
             * @param ticksPerRev The ticks per revolution
             * @return The builder
             */
            public Builder setTicksPerRev(double ticksPerRev) {
                twoWheelTrackingCoefficients.TICKS_PER_REV = ticksPerRev;
                return this;
            }

            /**
             * Set the radius of the tracking wheel.
             *
             * @param wheelRadius The radius of the tracking wheel
             * @return The builder
             */
            public Builder setWheelRadius(Measure<Distance> wheelRadius) {
                twoWheelTrackingCoefficients.WHEEL_RADIUS = wheelRadius.in(Inches);
                return this;
            }

            /**
             * Set the gear ratio of the tracking wheel.
             *
             * @param gearRatio The gear ratio of the tracking wheel (output wheel speed / input encoder speed)
             * @return The builder
             */
            public Builder setGearRatio(double gearRatio) {
                twoWheelTrackingCoefficients.GEAR_RATIO = gearRatio;
                return this;
            }

            /**
             * Set the position of the parallel wheel in the forward direction. +x forward.
             *
             * @param parallelX The position of the parallel wheel in the forward direction from the center of rotation
             * @return The builder
             */
            public Builder setParallelX(Measure<Distance> parallelX) {
                twoWheelTrackingCoefficients.PARALLEL_X = parallelX.in(Inches);
                return this;
            }

            /**
             * Set the position of the parallel wheel in the strafe direction. +y left.
             *
             * @param parallelY The position of the parallel wheel in the strafe direction from the center of rotation
             * @return The builder
             */
            public Builder setParallelY(Measure<Distance> parallelY) {
                twoWheelTrackingCoefficients.PARALLEL_Y = parallelY.in(Inches);
                return this;
            }

            /**
             * Set the position of the perpendicular wheel in the forward direction. +x forward.
             *
             * @param perpendicularX The position of the perpendicular wheel in the forward direction from the center of rotation
             * @return The builder
             */
            public Builder setPerpendicularX(Measure<Distance> perpendicularX) {
                twoWheelTrackingCoefficients.PERPENDICULAR_X = perpendicularX.in(Inches);
                return this;
            }

            /**
             * Set the position of the perpendicular wheel in the strafe direction. +y left.
             *
             * @param perpendicularY The position of the perpendicular wheel in the strafe direction from the center of rotation
             * @return The builder
             */
            public Builder setPerpendicularY(Measure<Distance> perpendicularY) {
                twoWheelTrackingCoefficients.PERPENDICULAR_Y = perpendicularY.in(Inches);
                return this;
            }

            /**
             * Set overflow compensation to be used on the localizer.
             *
             * @param correctEncoderCounts whether to use overflow compensation
             * @return The builder
             */
            public Builder setOverflowCompensation(boolean correctEncoderCounts) {
                twoWheelTrackingCoefficients.USE_CORRECTED_COUNTS = correctEncoderCounts;
                return this;
            }

            /**
             * Set the forward (x) multiplier for the ticks reported by the forward deadwheel.
             *
             * @param forwardMul the multiplier
             * @return The builder
             */
            public TwoWheelLocalizer.Coefficients.Builder setXMultiplier(double forwardMul) {
                twoWheelTrackingCoefficients.X_MULTIPLIER = forwardMul;
                return this;
            }

            /**
             * Set the strafe (y) multiplier for the ticks reported by the side deadwheel.
             *
             * @param strafeMul the multiplier
             * @return The builder
             */
            public TwoWheelLocalizer.Coefficients.Builder setYMultiplier(double strafeMul) {
                twoWheelTrackingCoefficients.Y_MULTIPLIER = strafeMul;
                return this;
            }

            /**
             * Build the coefficients.
             *
             * @return The coefficients
             */
            public TwoWheelLocalizer.Coefficients build() {
                return twoWheelTrackingCoefficients;
            }
        }
    }
}