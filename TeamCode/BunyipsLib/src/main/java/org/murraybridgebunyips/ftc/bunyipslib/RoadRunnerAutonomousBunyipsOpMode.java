package org.murraybridgebunyips.ftc.bunyipslib;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryAccelerationConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryVelocityConstraint;

import org.murraybridgebunyips.ftc.bunyipslib.roadrunner.drive.RoadRunnerDrive;
import org.murraybridgebunyips.ftc.bunyipslib.roadrunner.trajectorysequence.TrajectorySequence;
import org.murraybridgebunyips.ftc.bunyipslib.roadrunner.trajectorysequence.TrajectorySequenceBuilder;
import org.murraybridgebunyips.ftc.bunyipslib.tasks.RoadRunnerTask;

/**
 * Road Runner Autonomous Bunyips Op Mode (RRABOM, "rabone")
 * Additional abstraction for RoadRunner drives to integrate trajectories seamlessly into Autonomous.
 *
 * @author Lucas Bubner, 2023
 */
public abstract class RoadRunnerAutonomousBunyipsOpMode<T extends RoadRunnerDrive> extends AutonomousBunyipsOpMode {

    /**
     * Default timeout for RoadRunner tasks, set high to avoid the task being killed.
     * I suppose this value is not really "infinite", but if your task takes longer than 277.8 hours,
     * you have bigger problems than this.
     */
    public static final double INFINITE_TIMEOUT = 999999;

    /**
     * Drive instance to be used for RoadRunner trajectories.
     * You should assign this as you normally would, but instead relying on the superclass to handle
     * managing the class member.
     * <p>
     * {@code drive = new MecanumDrive(...)}
     */
    protected T drive;

    /**
     * Create a new builder for the custom RoadRunner trajectory, which will automatically add a
     * task to the queue when build() is called, optionally with a timeout control ({@link RoadRunnerTrajectoryTaskBuilder#setTimeout(double)}).
     * <p>
     * This method is the combination of {@link #newTrajectory()} and {@link #addTrajectory(Trajectory)}.
     *
     * @param startPose Starting pose of the trajectory
     * @return Builder for the trajectory
     */
    public RoadRunnerTrajectoryTaskBuilder addNewTrajectory(Pose2d startPose) {
        if (drive == null) throw new NullPointerException("drive instance is not set!");
        // noinspection rawtypes
        TrajectorySequenceBuilder builder = drive.trajectorySequenceBuilder(startPose);
        return new RoadRunnerTrajectoryTaskBuilder(startPose, builder.getBaseVelConstraint(), builder.getBaseAccelConstraint(), builder.getBaseTurnConstraintMaxAngVel(), builder.getBaseTurnConstraintMaxAngAccel());
    }

    /**
     * Create a new builder for the custom RoadRunner trajectory, which will automatically add a
     * task to the queue when build() is called, optionally with a timeout control.
     * This method is the combination of {@link #newTrajectory()} and {@link #addTrajectory(Trajectory)}.
     *
     * @return Builder for the trajectory
     * @see #addNewTrajectory(Pose2d)
     */
    public RoadRunnerTrajectoryTaskBuilder addNewTrajectory() {
        if (drive == null) throw new NullPointerException("drive instance is not set!");
        // noinspection rawtypes
        TrajectorySequenceBuilder builder = drive.trajectorySequenceBuilder(drive.getPoseEstimate());
        return new RoadRunnerTrajectoryTaskBuilder(drive.getPoseEstimate(), builder.getBaseVelConstraint(), builder.getBaseAccelConstraint(), builder.getBaseTurnConstraintMaxAngVel(), builder.getBaseTurnConstraintMaxAngAccel());
    }

    // Internal method to get the OpMode instance from an inner class
    private RoadRunnerAutonomousBunyipsOpMode<T> getOpMode() {
        return this;
    }

    /**
     * Create a new builder for a RoadRunner trajectory using the drive system.
     *
     * @param startPose Starting pose of the trajectory
     * @return Builder for the trajectory
     */
    public TrajectoryBuilder newTrajectory(Pose2d startPose) {
        if (drive == null) throw new NullPointerException("drive instance is not set!");
        return drive.trajectoryBuilder(startPose);
    }

    /**
     * Create a new builder for a RoadRunner trajectory using the drive system.
     *
     * @return Builder for the trajectory
     * @see #newTrajectory(Pose2d)
     */
    public TrajectoryBuilder newTrajectory() {
        if (drive == null) throw new NullPointerException("drive instance is not set!");
        return drive.trajectoryBuilder(drive.getPoseEstimate());
    }

    /**
     * Add a RoadRunner trajectory to the queue, with a default timeout.
     *
     * @param trajectory Trajectory to add
     */
    public void addTrajectory(Trajectory trajectory) {
        if (drive == null) throw new NullPointerException("drive instance is not set!");
        addTask(new RoadRunnerTask<>(this, INFINITE_TIMEOUT, drive, trajectory));
    }

    /**
     * Add a RoadRunner trajectory to the queue, with a default timeout.
     *
     * @param trajectorySequence Trajectory to add
     */
    public void addTrajectory(TrajectorySequence trajectorySequence) {
        if (drive == null) throw new NullPointerException("drive instance is not set!");
        addTask(new RoadRunnerTask<>(this, INFINITE_TIMEOUT, drive, trajectorySequence));
    }

    /**
     * Add a RoadRunner trajectory to the queue, with a task timeout other than the default.
     *
     * @param trajectory Trajectory to add
     * @param timeout    Timeout in seconds
     */
    public void addTrajectory(Trajectory trajectory, double timeout) {
        if (drive == null) throw new NullPointerException("drive instance is not set!");
        addTask(new RoadRunnerTask<>(this, timeout, drive, trajectory));
    }

    /**
     * Add a RoadRunner trajectory to the queue, with a task timeout other than the default.
     *
     * @param trajectorySequence Trajectory to add
     * @param timeout            Timeout in seconds
     */
    public void addTrajectory(TrajectorySequence trajectorySequence, double timeout) {
        if (drive == null) throw new NullPointerException("drive instance is not set!");
        addTask(new RoadRunnerTask<>(this, timeout, drive, trajectorySequence));
    }

    /**
     * Builder class for a RoadRunner trajectory, which supports adding the trajectory to the Task queue.
     */
    protected class RoadRunnerTrajectoryTaskBuilder extends TrajectorySequenceBuilder<RoadRunnerTrajectoryTaskBuilder> {
        private double timeout = INFINITE_TIMEOUT;

        public RoadRunnerTrajectoryTaskBuilder(Pose2d startPose, Double startTangent, TrajectoryVelocityConstraint baseVelConstraint, TrajectoryAccelerationConstraint baseAccelConstraint, double baseTurnConstraintMaxAngVel, double baseTurnConstraintMaxAngAccel) {
            super(startPose, startTangent, baseVelConstraint, baseAccelConstraint, baseTurnConstraintMaxAngVel, baseTurnConstraintMaxAngAccel);
        }

        public RoadRunnerTrajectoryTaskBuilder(Pose2d startPose, TrajectoryVelocityConstraint baseVelConstraint, TrajectoryAccelerationConstraint baseAccelConstraint, double baseTurnConstraintMaxAngVel, double baseTurnConstraintMaxAngAccel) {
            super(startPose, baseVelConstraint, baseAccelConstraint, baseTurnConstraintMaxAngVel, baseTurnConstraintMaxAngAccel);
        }

        /**
         * Set a timeout for the trajectory, to be applied to the overhead task running the trajectory.
         * Should be called first, before any other builder methods.
         * If this method is not called or fed negative values, an infinite timeout will be used.
         *
         * @param timeout Timeout in seconds
         * @return trajectory builder
         */
        // javascript reference incoming
        public RoadRunnerTrajectoryTaskBuilder setTimeout(double timeout) {
            // javascript reference is done
            if (timeout < 0) {
                return this;
            }
            this.timeout = timeout;
            return this;
        }

        @Override
        public TrajectorySequence build() {
            if (drive == null) throw new NullPointerException("drive instance is not set!");
            TrajectorySequence builtTrajectory = super.build();
            addTask(new RoadRunnerTask<>(getOpMode(), timeout, drive, builtTrajectory));
            return builtTrajectory;
        }
    }
}