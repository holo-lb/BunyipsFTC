package org.murraybridgebunyips.glados.debug;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.murraybridgebunyips.bunyipslib.AutonomousBunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.Controls;
import org.murraybridgebunyips.bunyipslib.Reference;
import org.murraybridgebunyips.bunyipslib.RoadRunner;
import org.murraybridgebunyips.bunyipslib.Storage;
import org.murraybridgebunyips.bunyipslib.drive.DualDeadwheelMecanumDrive;
import org.murraybridgebunyips.bunyipslib.drive.MecanumDrive;
import org.murraybridgebunyips.glados.components.GLaDOSConfigCore;

/**
 * Test to learn RoadRunner
 *
 * @author Lachlan Paul, 2023
 */
@Autonomous(name = "RoadRunner Test")
@Disabled
public class GLaDOSRoadRunnerTest extends AutonomousBunyipsOpMode implements RoadRunner {
    private final GLaDOSConfigCore config = new GLaDOSConfigCore();
    private DualDeadwheelMecanumDrive drive;

    @Override
    protected void onInitialise() {
        config.init();
        drive = new DualDeadwheelMecanumDrive(config.driveConstants, config.mecanumCoefficients, config.imu, config.frontLeft, config.frontRight, config.backLeft, config.backRight, config.localizerCoefficients, config.parallelDeadwheel, config.perpendicularDeadwheel);
        Storage.memory().lastKnownPosition = null;
    }

    @NonNull
    @Override
    public MecanumDrive getDrive() {
        return drive;
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, Controls selectedButton) {
        // Start red-left
        makeTrajectory(new Pose2d(-37.95, -71.22, Math.toRadians(90.00)))
                .splineTo(new Vector2d(-35.77, -13.74), Math.toRadians(87.16))
                .splineTo(new Vector2d(3.44, 3.12), Math.toRadians(27.30))
                .splineTo(new Vector2d(45.61, 58.10), Math.toRadians(52.51))
                .splineTo(new Vector2d(41.39, -3.28), Math.toRadians(264.54))
                .splineTo(new Vector2d(37.02, -40.76), Math.toRadians(181.15))
                .splineTo(new Vector2d(-49.20, -36.23), Math.toRadians(180.18))
                .splineTo(new Vector2d(-37.95, -61.07), Math.toRadians(-87.75))
                .addTask();

    }
}
