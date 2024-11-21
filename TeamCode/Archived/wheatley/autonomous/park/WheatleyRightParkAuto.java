package org.murraybridgebunyips.wheatley.autonomous.park;

import static org.murraybridgebunyips.bunyipslib.external.units.Units.Centimeters;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.FieldTiles;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Seconds;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.murraybridgebunyips.bunyipslib.AutonomousBunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.Controls;
import org.murraybridgebunyips.bunyipslib.Reference;
import org.murraybridgebunyips.bunyipslib.RoadRunner;
import org.murraybridgebunyips.bunyipslib.StartingPositions;
import org.murraybridgebunyips.bunyipslib.drive.MecanumDrive;
import org.murraybridgebunyips.bunyipslib.tasks.MessageTask;
import org.murraybridgebunyips.wheatley.components.WheatleyConfig;

/**
 * Parking autonomous for Wheatley
 * This Auto is for when you are parking on the RIGHT of your alliance backdrop
 * Make sure to coordinate with your alliance before selecting an Autonomous
 * <p></p>
 * A for Short Red<p></p>
 * B for Short Blue<p></p>
 * X for Long Red<p></p>
 * Y for Long Blue<p></p>
 *
 * @author Lachlan Paul, 2024
 */

@Autonomous(name = "Right Park Auto")
public class WheatleyRightParkAuto extends AutonomousBunyipsOpMode implements RoadRunner {
    private final WheatleyConfig config = new WheatleyConfig();
    private MecanumDrive drive;

    @Override
    protected void onInitialise() {
        config.init();
        drive = new MecanumDrive(
                config.driveConstants, config.mecanumCoefficients,
                config.imu,
                config.fl, config.fr, config.bl, config.br
        );
        setOpModes(StartingPositions.use());
    }

    @NonNull
    @Override
    public MecanumDrive getDrive() {
        return drive;
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, Controls selectedButton) {
        if (selectedOpMode == null) {
            return;
        }

        switch ((StartingPositions) selectedOpMode.require()) {
            case STARTING_RED_LEFT:
                addTask(new MessageTask(Seconds.of(15), "If the robot is not moving DO NOT PANIC, it is waiting for others to move"));
                makeTrajectory()
                        .forward(5)
                        .addTask();
                makeTrajectory()
                        .strafeRight(180, Centimeters)
                        .addTask();
                makeTrajectory()
                        .back(2)
                        .addTask();
                makeTrajectory()
                        .strafeRight(100)
                        .addTask();
                break;

            case STARTING_BLUE_LEFT:
                makeTrajectory()
                        .forward(3, FieldTiles)
                        .addTask();
                makeTrajectory()
                        .strafeLeft(3, FieldTiles)
                        .addTask();
                break;

            case STARTING_RED_RIGHT:
                makeTrajectory()
                        .strafeRight(180, Centimeters)
                        .addTask();
                break;

            case STARTING_BLUE_RIGHT:
                addTask(new MessageTask(Seconds.of(15), "If the robot is not moving DO NOT PANIC, it is waiting for others to move"));
                makeTrajectory()
                        .forward(3, FieldTiles)
                        .addTask();
                makeTrajectory()
                        .strafeLeft(5.5, FieldTiles)
                        .addTask();
                break;
        }
    }
}
