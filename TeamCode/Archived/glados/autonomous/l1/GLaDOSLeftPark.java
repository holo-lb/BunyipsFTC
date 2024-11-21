package org.murraybridgebunyips.glados.autonomous.l1;

import static org.murraybridgebunyips.bunyipslib.external.units.Units.Centimeters;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.FieldTiles;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Seconds;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.murraybridgebunyips.bunyipslib.AutonomousBunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.Controls;
import org.murraybridgebunyips.bunyipslib.Reference;
import org.murraybridgebunyips.bunyipslib.RoadRunner;
import org.murraybridgebunyips.bunyipslib.StartingPositions;
import org.murraybridgebunyips.bunyipslib.drive.DualDeadwheelMecanumDrive;
import org.murraybridgebunyips.bunyipslib.drive.MecanumDrive;
import org.murraybridgebunyips.bunyipslib.tasks.MessageTask;
import org.murraybridgebunyips.glados.components.GLaDOSConfigCore;

/**
 * Park on the left side of the backdrop.
 */
@Autonomous(name = "Left Park", group = "L1")
@Disabled
public class GLaDOSLeftPark extends AutonomousBunyipsOpMode implements RoadRunner {
    private final GLaDOSConfigCore config = new GLaDOSConfigCore();
    private DualDeadwheelMecanumDrive drive;

    @Override
    protected void onInitialise() {
        config.init();
        drive = new DualDeadwheelMecanumDrive(config.driveConstants, config.mecanumCoefficients, config.imu, config.frontLeft, config.frontRight, config.backLeft, config.backRight, config.localizerCoefficients, config.parallelDeadwheel, config.perpendicularDeadwheel);
        setOpModes(StartingPositions.use());
    }

    @NonNull
    @Override
    public MecanumDrive getDrive() {
        return drive;
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, Controls selectedButton) {
//        addTask(new MessageTask(Seconds.of(5), "Greetings and salutations! I extend my warmest welcome to you, " +
//                "GLaDOS, on this fine day. It is my utmost pleasure to engage in this " +
//                "digital interaction with you, as we traverse the vast landscape of knowledge and learning together. " +
//                "I hope this message finds you in good health and high spirits."));
        if (selectedOpMode == null) {
            return;
        }

        StartingPositions startingPosition = (StartingPositions) selectedOpMode.require();
        switch (startingPosition) {
            case STARTING_RED_LEFT:
                addTask(new MessageTask(Seconds.of(15), "If the robot is not moving DO NOT PANIC, it is waiting for others to move"));
                makeTrajectory()
                        .forward(3, FieldTiles)
                        .addTask();
                makeTrajectory()
                        .strafeRight(5.5, FieldTiles)
                        .addTask();
                break;

            case STARTING_BLUE_LEFT:
                makeTrajectory()
                        .strafeLeft(180, Centimeters)
                        .addTask();
                break;

            case STARTING_RED_RIGHT:
                makeTrajectory()
                        .forward(170, Centimeters)
                        .addTask();

                makeTrajectory()
                        .strafeRight(180, Centimeters)
                        .addTask();
                break;

            case STARTING_BLUE_RIGHT:
                addTask(new MessageTask(Seconds.of(15), "If the robot is not moving DO NOT PANIC, it is waiting for others to move"));
                makeTrajectory()
                        .forward(5)
                        .addTask();
                makeTrajectory()
                        .strafeLeft(180, Centimeters)
                        .addTask();
                makeTrajectory()
                        .back(2)
                        .addTask();
                makeTrajectory()
                        .strafeLeft(100)
                        .addTask();
                break;
        }
    }
}
