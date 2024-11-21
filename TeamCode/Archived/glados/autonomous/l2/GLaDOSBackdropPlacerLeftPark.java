package org.murraybridgebunyips.glados.autonomous.l2;

import static org.murraybridgebunyips.bunyipslib.external.units.Units.Centimeters;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Degrees;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.FieldTile;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.FieldTiles;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Second;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Seconds;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.murraybridgebunyips.bunyipslib.AutonomousBunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.Controls;
import org.murraybridgebunyips.bunyipslib.Reference;
import org.murraybridgebunyips.bunyipslib.RoadRunner;
import org.murraybridgebunyips.bunyipslib.StartingPositions;
import org.murraybridgebunyips.bunyipslib.drive.DualDeadwheelMecanumDrive;
import org.murraybridgebunyips.bunyipslib.roadrunner.drive.RoadRunnerDrive;
import org.murraybridgebunyips.bunyipslib.roadrunner.trajectorysequence.TrajectorySequence;
import org.murraybridgebunyips.bunyipslib.subsystems.DualServos;
import org.murraybridgebunyips.bunyipslib.subsystems.HoldableActuator;
import org.murraybridgebunyips.bunyipslib.tasks.RoadRunnerTask;
import org.murraybridgebunyips.bunyipslib.tasks.WaitTask;
import org.murraybridgebunyips.bunyipslib.tasks.groups.ParallelTaskGroup;
import org.murraybridgebunyips.glados.components.GLaDOSConfigCore;

/**
 * Backdrop Placer Autonomous for Left Parking, no AprilTags
 *
 * @author Lucas Bubner, 2024
 */
@Config
@Autonomous(name = "Backdrop Placer (Left Park, NO VISION)", group = "L2")
public class GLaDOSBackdropPlacerLeftPark extends AutonomousBunyipsOpMode implements RoadRunner {
    /**
     * Position delta (in ticks) of the arm extension at backboard
     */
    public static int ARM_DELTA = 1500;

    private final GLaDOSConfigCore config = new GLaDOSConfigCore();
    private DualDeadwheelMecanumDrive drive;
    private HoldableActuator arm;
    private DualServos claws;

    @Override
    protected void onInitialise() {
        config.init();
        drive = new DualDeadwheelMecanumDrive(config.driveConstants, config.mecanumCoefficients, config.imu, config.frontLeft, config.frontRight, config.backLeft, config.backRight, config.localizerCoefficients, config.parallelDeadwheel, config.perpendicularDeadwheel);
        arm = new HoldableActuator(config.arm).withMovingPower(0.5);
        claws = new DualServos(config.leftPixel, config.rightPixel, 1.0, 0.0, 0.0, 1.0);

        setOpModes(StartingPositions.use());
    }

    @NonNull
    @Override
    public RoadRunnerDrive getDrive() {
        return drive;
    }

    // Set which direction the robot will strafe at the backdrop. Overridden in the right park variant.
    protected RoadRunnerTask afterPixelDropDriveAction(RoadRunnerTrajectoryTaskBuilder builder) {
        return builder
                .strafeLeft(0.95, FieldTile)
                .buildTask();
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, Controls selectedButton) {
        if (selectedOpMode == null)
            return;

        StartingPositions startingPosition = (StartingPositions) selectedOpMode.require();
        // Facing FORWARD from the starting position as selected
        setPose(startingPosition.getPose());

        // Go to backdrop
        Reference<TrajectorySequence> blueRight = Reference.empty();
        TrajectorySequence redLeft = makeTrajectory()
                .forward(2, FieldTiles)
                .strafeRight(3, FieldTiles)
                .turn(-Math.PI / 2)
                .strafeRight(1, FieldTile)
                .mirrorToRef(blueRight)
                .build();
        TrajectorySequence redRight = makeTrajectory()
                .lineToLinearHeading(startingPosition.getPose()
                        .plus(RoadRunner.unitPose(new Pose2d(1, 1, -90), FieldTiles, Degrees)))
                .build();
        TrajectorySequence blueLeft = makeTrajectory()
                .lineToLinearHeading(startingPosition.getPose()
                        .plus(RoadRunner.unitPose(new Pose2d(1, -1, 90), FieldTiles, Degrees)))
                .build();

        TrajectorySequence targetSequence = null;
        switch (startingPosition) {
            case STARTING_RED_LEFT:
                targetSequence = redLeft;
                break;
            case STARTING_RED_RIGHT:
                targetSequence = redRight;
                break;
            case STARTING_BLUE_LEFT:
                targetSequence = blueLeft;
                break;
            case STARTING_BLUE_RIGHT:
                targetSequence = blueRight.require();
                break;
        }
        assert targetSequence != null;
        makeTrajectory()
                .runSequence(targetSequence)
                .withName("Navigate to Backdrop")
                .addTask();

        makeTrajectory()
                .forward(40, Centimeters)
                .withName("Forward Align Backdrop")
                .addTask();

        // Place pixels and park to the left of the backdrop
        addTask(arm.tasks.delta(ARM_DELTA).withTimeout(Seconds.of(2)).withName("Deploy Arm"));
        addTask(claws.tasks.openBoth().withName("Drop Pixels"));
        addTask(new WaitTask(Seconds.of(1)).withName("Wait for Pixels"));
        addTask(new ParallelTaskGroup(
                afterPixelDropDriveAction(makeTrajectory()),
                arm.tasks.delta(-ARM_DELTA).withTimeout(Seconds.of(2))
        ).withName("Stow and Move to Park"));

        makeTrajectory()
                .forward(0.98, FieldTiles)
                .setVelConstraint(atVelocity(0.1, FieldTiles.per(Second)))
                .withName("Finish Park")
                .addTask();
    }
}
