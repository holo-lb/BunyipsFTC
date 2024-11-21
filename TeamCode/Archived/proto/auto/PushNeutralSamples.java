package org.murraybridgebunyips.proto.auto;

import static org.murraybridgebunyips.bunyipslib.StartingConfiguration.blueLeft;
import static org.murraybridgebunyips.bunyipslib.StartingConfiguration.redLeft;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Centimeters;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Degrees;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.FieldTiles;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Inches;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.murraybridgebunyips.bunyipslib.AutonomousBunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.Controls;
import org.murraybridgebunyips.bunyipslib.Reference;
import org.murraybridgebunyips.bunyipslib.RoadRunner;
import org.murraybridgebunyips.bunyipslib.StartingConfiguration;
import org.murraybridgebunyips.bunyipslib.drive.DualDeadwheelMecanumDrive;
import org.murraybridgebunyips.bunyipslib.roadrunner.drive.RoadRunnerDrive;
import org.murraybridgebunyips.bunyipslib.roadrunner.trajectorysequence.TrajectorySequence;
import org.murraybridgebunyips.proto.Proto;

/**
 * Base drive trajectory to push all neutral samples from the LEFT side of the field into the corner.
 * Includes an initial push for the preload element.
 *
 * @author Lucas Bubner, 2024
 */
@Autonomous(name = "1+3 Neutral Sample Base Route")
public class PushNeutralSamples extends AutonomousBunyipsOpMode implements RoadRunner {
    private final Proto robot = new Proto();

    private DualDeadwheelMecanumDrive drive;

    @Override
    protected void onInitialise() {
        robot.init();
        drive = new DualDeadwheelMecanumDrive(
                robot.driveConstants, robot.mecanumCoefficients, robot.imu, robot.frontLeft, robot.frontRight, robot.backLeft,
                robot.backRight, robot.localizerCoefficients, robot.parallelDeadwheel, robot.perpendicularDeadwheel
        );

        setOpModes(
                redLeft().tile(2).backward(Centimeters.of(7)).rotate(Degrees.of(90)),
                blueLeft().tile(2).backward(Centimeters.of(7)).rotate(Degrees.of(90))
        );
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, Controls selectedButton) {
        if (selectedOpMode == null) return;
        StartingConfiguration.Position startingPosition = (StartingConfiguration.Position) selectedOpMode.require();

        setPose(startingPosition.toFieldPose());
        Reference<TrajectorySequence> blue = Reference.empty();
        TrajectorySequence red = makeTrajectory()
                .setRefMirroring(MirrorMap.SYMMETRIC_MIRROR)
                .mirrorToRef(blue)
                .forward(0.8, FieldTiles)
                .lineToSplineHeading(new Pose2d(-31.14, -26.09, 180.00), Inches, Degrees)
                .lineToSplineHeading(new Pose2d(-44.91, -13.08, 250.00), Inches, Degrees)
                .splineToLinearHeading(new Pose2d(-56.24, -51.49, 233), Inches, Degrees, 233, Degrees)
                .setReversed(true)
                .splineTo(new Vector2d(-44.91, -13.08), Inches, 250 - 180, Degrees)
                .setReversed(false)
                .lineToLinearHeading(new Pose2d(-56.39, -9.41, 260.22), Inches, Degrees)
                .splineTo(new Vector2d(-59.60, -53.02), Inches, 248.37, Degrees)
                .setReversed(true)
                .splineTo(new Vector2d(-56.39, -9.41), Inches, 260.22 - 180, Degrees)
                .setReversed(false)
                .lineToLinearHeading(new Pose2d(-63.12, -7.57, 270.00), Inches, Degrees)
                .splineTo(new Vector2d(-62.51, -55.47), Inches, 270.00, Degrees)
                .lineTo(new Vector2d(-24.56, -11.25), Inches)
                .build();

        addTask(makeTrajectory().runSequence(startingPosition.isRed() ? red : blue.get()).buildTask());
    }

    @NonNull
    @Override
    public RoadRunnerDrive getDrive() {
        return drive;
    }
}
