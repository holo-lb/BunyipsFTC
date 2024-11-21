package org.murraybridgebunyips.glados.debug;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.murraybridgebunyips.bunyipslib.BunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.drive.DualDeadwheelMecanumDrive;
import org.murraybridgebunyips.bunyipslib.drive.MecanumDrive;
import org.murraybridgebunyips.glados.components.GLaDOSConfigCore;

/**
 * POV drivetrain only for GLaDOS.
 *
 * @author Lucas Bubner, 2023
 */
@TeleOp(name = "Drivetrain")
@Disabled
public class GLaDOSDrivetrain extends BunyipsOpMode {
    private final GLaDOSConfigCore config = new GLaDOSConfigCore();
    private MecanumDrive drive;

    @Override
    protected void onInit() {
        config.init();
        drive = new DualDeadwheelMecanumDrive(
                config.driveConstants, config.mecanumCoefficients,
                config.imu, config.frontLeft, config.frontRight,
                config.backLeft, config.backRight, config.localizerCoefficients,
                config.parallelDeadwheel, config.perpendicularDeadwheel
        );
    }

    @Override
    protected void activeLoop() {
        double x = gamepad1.left_stick_x;
        double y = gamepad1.left_stick_y;
        double r = gamepad1.right_stick_x;
        drive.setSpeedUsingController(x, y, r);
        drive.update();
    }
}

