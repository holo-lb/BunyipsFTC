package org.murraybridgebunyips.proto.auto;

import static org.murraybridgebunyips.bunyipslib.external.units.Units.Degrees;
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
import org.murraybridgebunyips.bunyipslib.drive.DualDeadwheelMecanumDrive;
import org.murraybridgebunyips.bunyipslib.roadrunner.drive.RoadRunnerDrive;
import org.murraybridgebunyips.proto.Proto;

/**
 * Specimen placing base route.
 *
 * @author Lucas Bubner, 2024
 */
@Autonomous(name = "1+3 Specimen Base Route (Red Right, Tile #2.5 back touching)")
public class SpecimenPath extends AutonomousBunyipsOpMode implements RoadRunner {
    private final Proto robot = new Proto();

    private DualDeadwheelMecanumDrive drive;

    @Override
    protected void onInitialise() {
        robot.init();
        drive = new DualDeadwheelMecanumDrive(
                robot.driveConstants, robot.mecanumCoefficients, robot.imu, robot.frontLeft, robot.frontRight, robot.backLeft,
                robot.backRight, robot.localizerCoefficients, robot.parallelDeadwheel, robot.perpendicularDeadwheel
        );
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, Controls selectedButton) {
        setPose(new Pose2d(24.10, -62.82, 90.00), Inches, Degrees);
        makeTrajectory()
                .lineTo(new Vector2d(0.23, -33.59), Inches)
                .lineTo(new Vector2d(48.89, -38.33), Inches)
                .lineTo(new Vector2d(-0.23, -33.59), Inches)
                .lineToLinearHeading(new Pose2d(54.86, -37.87, 53.75), Inches, Degrees)
                .lineToLinearHeading(new Pose2d(-0.08, -33.59, 90.00), Inches, Degrees)
                .lineToLinearHeading(new Pose2d(60.06, -38.18, 40.00), Inches, Degrees)
                .lineToLinearHeading(new Pose2d(-0.08, -33.74, 90.00), Inches, Degrees)
                .lineToConstantHeading(new Vector2d(-24.41, -36.34), Inches)
                .splineTo(new Vector2d(-24.71, -16.60), Inches, 80.00, Degrees)
                .addTask();
    }

    @NonNull
    @Override
    public RoadRunnerDrive getDrive() {
        return drive;
    }
}
