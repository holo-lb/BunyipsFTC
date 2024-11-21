package org.murraybridgebunyips.glados.debug;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.murraybridgebunyips.bunyipslib.roadrunner.drive.MecanumRoadRunnerDrive;
import org.murraybridgebunyips.bunyipslib.roadrunner.drive.RoadRunnerDrive;
import org.murraybridgebunyips.bunyipslib.roadrunner.drive.localizers.TwoWheelLocalizer;
import org.murraybridgebunyips.bunyipslib.roadrunner.drive.tuning.RoadRunnerTuning;
import org.murraybridgebunyips.glados.components.GLaDOSConfigCore;

/**
 * Tuning wrapper for RoadRunner.
 */
@TeleOp(name = "RoadRunner Tuning")
//@Disabled
public class GLaDOSRoadRunnerTuning extends RoadRunnerTuning {
    @NonNull
    @Override
    protected RoadRunnerDrive getBaseRoadRunnerDrive() {
        GLaDOSConfigCore config = new GLaDOSConfigCore();
        config.init(this);
        MecanumRoadRunnerDrive drive = new MecanumRoadRunnerDrive(null, config.driveConstants, config.mecanumCoefficients, hardwareMap.voltageSensor, config.imu, config.frontLeft, config.frontRight, config.backLeft, config.backRight);
        drive.setLocalizer(new TwoWheelLocalizer(config.localizerCoefficients, config.parallelDeadwheel, config.perpendicularDeadwheel, drive));
        return drive;
    }
}
