package org.murraybridgebunyips.wheatley.teleop;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.murraybridgebunyips.bunyipslib.BunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.Storage;
import org.murraybridgebunyips.bunyipslib.drive.MecanumDrive;
import org.murraybridgebunyips.wheatley.components.WheatleyConfig;

/**
 * RoadRunner Mecanum Drive test for TeleOp
 */
@TeleOp(name = "RoadRunner Mecanum Drive")
@Disabled
public class WheatleyRRMecanum extends BunyipsOpMode {
    private final WheatleyConfig config = new WheatleyConfig();
    private MecanumDrive drive;

    @Override
    protected void onInit() {
        config.init();
        Storage.memory().lastKnownPosition = null;
        drive = new MecanumDrive(
                config.driveConstants, config.mecanumCoefficients,
                config.imu, config.fl, config.fr, config.bl, config.br
        );
    }

    @Override
    protected void activeLoop() {
        drive.setSpeedUsingController(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
        drive.update();
    }

    @Override
    protected void onStop() {
        drive.stop();
    }
}