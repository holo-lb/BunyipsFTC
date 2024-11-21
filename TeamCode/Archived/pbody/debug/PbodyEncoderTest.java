package org.murraybridgebunyips.pbody.debug;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.murraybridgebunyips.bunyipslib.BunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.drive.CartesianMecanumDrive;
import org.murraybridgebunyips.pbody.components.PbodyConfig;

/**
 * Tests encoders using the drive
 */
@TeleOp(name = "EncTest")
@Disabled
public class PbodyEncoderTest extends BunyipsOpMode {
    private final PbodyConfig config = new PbodyConfig();
    private CartesianMecanumDrive drive;

    @Override
    protected void onInit() {
        config.init();
        drive = new CartesianMecanumDrive(config.fl, config.fr, config.bl, config.br);
    }

    @Override
    protected void activeLoop() {
        telemetry.add("fl:% fr:% bl:% br:%", config.fl.getCurrentPosition(), config.fr.getCurrentPosition(), config.bl.getCurrentPosition(), config.br.getCurrentPosition());
        drive.setSpeedUsingController(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x).update();
    }
}
