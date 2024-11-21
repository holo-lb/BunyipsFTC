package org.murraybridgebunyips.pbody.teleop;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.murraybridgebunyips.bunyipslib.BunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.drive.CartesianMecanumDrive;
import org.murraybridgebunyips.pbody.components.PbodyConfig;

/**
 * Primary TeleOp, ported from Blocks to BunyipsLib
 *
 * @author Jeff Rabone, 2023 (original Blocks author)
 */
@TeleOp(name = "TeleOp(Blocks)")
@Disabled
public class PbodyTeleOpBlocks extends BunyipsOpMode {
    private final PbodyConfig config = new PbodyConfig();
    private CartesianMecanumDrive drive;

    @Override
    protected void onInit() {
        config.init();
        drive = new CartesianMecanumDrive(config.fl, config.fr, config.bl, config.br);

        config.pl.setPosition(0.6);
        config.ls.setPosition(0.6);
        config.rs.setPosition(0.7);
    }

    @Override
    protected void activeLoop() {
        // close rs
        if (gamepad2.y) {
            config.rs.setPosition(0.7);
        }
        // open rs
        if (gamepad2.b) {
            config.rs.setPosition(0.4);
        }
        // close ls
        if (gamepad2.a) {
            config.ls.setPosition(0.9);
        }
        // open ls
        if (gamepad2.x) {
            config.ls.setPosition(0.6);
        }
        // drone
        if (gamepad1.y) {
            config.pl.setPosition(0.6);
        }
        if (gamepad1.b) {
            config.pl.setPosition(0);
        }

        config.arm.setPower(-0.6 * gamepad2.right_stick_y);

        drive.setSpeedUsingController(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
        drive.update();
    }
}
