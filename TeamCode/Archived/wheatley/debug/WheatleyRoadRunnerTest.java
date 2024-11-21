package org.murraybridgebunyips.wheatley.debug;

import static org.murraybridgebunyips.bunyipslib.external.units.Units.Degrees;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Meter;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Meters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.murraybridgebunyips.bunyipslib.AutonomousBunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.Controls;
import org.murraybridgebunyips.bunyipslib.Reference;
import org.murraybridgebunyips.bunyipslib.RoadRunner;
import org.murraybridgebunyips.bunyipslib.Storage;
import org.murraybridgebunyips.bunyipslib.drive.MecanumDrive;
import org.murraybridgebunyips.wheatley.components.WheatleyConfig;

/**
 * Test for RoadRunner.
 */
@Autonomous(name = "RoadRunner Test")
@Disabled
public class WheatleyRoadRunnerTest extends AutonomousBunyipsOpMode implements RoadRunner {
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
        Storage.memory().lastKnownPosition = null;
    }

    @NonNull
    @Override
    public MecanumDrive getDrive() {
        return drive;
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, Controls selectedButton) {
        makeTrajectory()
                .forward(1, Meter)
                .turn(180, Degrees)
                .back(1, Meter)
                .forward(2, Meters)
                .turn(180, Degrees)
                .addTask();
    }
}
