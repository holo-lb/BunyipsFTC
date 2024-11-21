package org.murraybridgebunyips.pbody.debug;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.murraybridgebunyips.bunyipslib.roadrunner.drive.MecanumRoadRunnerDrive;
import org.murraybridgebunyips.bunyipslib.roadrunner.drive.RoadRunnerDrive;
import org.murraybridgebunyips.bunyipslib.roadrunner.drive.tuning.RoadRunnerTuningOpMode;
import org.murraybridgebunyips.pbody.components.PbodyConfig;

/**
 * RoadRunner tuning
 */
@TeleOp(name = "RRTuner")
@Disabled
public class PbodyRoadRunnerTuner extends RoadRunnerTuningOpMode {
    private final PbodyConfig config = new PbodyConfig();

    @NonNull
    @Override
    public RoadRunnerDrive getBaseRoadRunnerDrive() {
        config.init(this);
        return new MecanumRoadRunnerDrive(null, config.driveConstants, config.mecanumCoefficients, hardwareMap.voltageSensor, config.imu, config.fl, config.fr, config.bl, config.br);
    }
}
