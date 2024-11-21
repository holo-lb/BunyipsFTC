package org.murraybridgebunyips.wheatley.debug;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.murraybridgebunyips.bunyipslib.roadrunner.drive.MecanumRoadRunnerDrive;
import org.murraybridgebunyips.bunyipslib.roadrunner.drive.RoadRunnerDrive;
import org.murraybridgebunyips.bunyipslib.roadrunner.drive.tuning.RoadRunnerTuningOpMode;
import org.murraybridgebunyips.wheatley.components.WheatleyConfig;

/**
 * Tuning wrapper for RoadRunner.
 */
@Autonomous(name = "RoadRunner Test")
@Disabled
public class WheatleyRoadRunnerTuning extends RoadRunnerTuningOpMode {
    @NonNull
    @Override
    public RoadRunnerDrive getBaseRoadRunnerDrive() {
        WheatleyConfig ROBOT_CONFIG = new WheatleyConfig();
        ROBOT_CONFIG.init(this);
        return new MecanumRoadRunnerDrive(null, ROBOT_CONFIG.driveConstants, ROBOT_CONFIG.mecanumCoefficients, hardwareMap.voltageSensor, ROBOT_CONFIG.imu, ROBOT_CONFIG.fl, ROBOT_CONFIG.fr, ROBOT_CONFIG.bl, ROBOT_CONFIG.br);
    }
}
