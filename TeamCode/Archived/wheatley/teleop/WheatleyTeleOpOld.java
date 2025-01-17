package org.murraybridgebunyips.wheatley.teleop;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.murraybridgebunyips.bunyipslib.BunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.subsystems.Cannon;
import org.murraybridgebunyips.bunyipslib.subsystems.DualServos;
import org.murraybridgebunyips.bunyipslib.drive.MecanumDrive;
import org.murraybridgebunyips.bunyipslib.subsystems.Rotator;
import org.murraybridgebunyips.wheatley.components.WheatleyConfig;

/**
 * TeleOp for all of Wheatley's functions.
 * <p></p>
 * gamepad1:<br>
 * left_stick_x: strafe<br>
 * left_stick_y: forward/backward<br>
 * right_stick_x: turn<br>
 * right_trigger: fire cannon<br>
 * options: reset cannon<br>
 * <p></p>
 * gamepad2:<br>
 * x: toggle left claw<br>
 * b: toggle right claw<br>
 * left_stick_y: actuate the management rail<br>
 * right_stick_y: move claw mover<br>
 * dpad_up: extend hook one position<br>
 * dpad_down: retract hook one position<br> 2dew: update
 *
 * @author Lachlan Paul, 2024
 * @author Lucas Bubner, 2024
 */

@TeleOp(name = "TeleOp Old")
@Disabled
public class WheatleyTeleOpOld extends BunyipsOpMode {
    private final WheatleyConfig config = new WheatleyConfig();
    private MecanumDrive drive;
    private Cannon cannon;
    private PersonalityCoreLinearActuator linearActuator;
    private Rotator rotator;
    private DualServos claws;

    private boolean xPressed;
    private boolean bPressed;

    @Override
    protected void onInit() {
        config.init();
        drive = new MecanumDrive(
                config.driveConstants, config.mecanumCoefficients,
                hardwareMap.voltageSensor, config.imu, config.fl, config.fr, config.bl, config.br
        );
        cannon = new Cannon(config.launcher);
        linearActuator = new PersonalityCoreLinearActuator(config.linearActuator);
        rotator = new Rotator(config.clawRotator);
        claws = new DualServos(config.leftPixel, config.rightPixel, 1.0, 0.0, 1.0, 0.0);
    }

    @Override
    protected void activeLoop() {
        drive.setSpeedUsingController(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);

        // Launches the paper plane
        // The triggers are pressure sensitive, apparently.
        // Set to 1 to avoid any slight touches launching a nuke.
        if (gamepad1.right_trigger == 1.0) {
            // "Now then, let's see what we got here. Ah! 'Reactor Core Emergency Heat Venting Protocols.'
            // that's the problem right there, isn't it? 'Emergency'. you don't want to see 'emergency'
            // flashing at you. Never good that, is it? Right. DELETE."
            cannon.fire();
        }

        // Reset cannon for debugging purposes
        if (gamepad1.back) {
            // "Undelete, undelete! Where's the undelete button?"
            cannon.reset();
        }

        // Claw controls
        if (gamepad2.x && !xPressed) {
            claws.toggleServo(DualServos.ServoSide.LEFT);
        } else if (gamepad2.b && !bPressed) {
            claws.toggleServo(DualServos.ServoSide.RIGHT);
        }

        linearActuator.setPower(-gamepad2.left_stick_y);
        rotator.setPower(-gamepad2.right_stick_y);

        // Register actions only once per press
        xPressed = gamepad2.x;
        bPressed = gamepad2.b;

        // Send stateful updates to the hardware
        drive.update();
        claws.update();
        linearActuator.update();
        rotator.update();
        cannon.update();
    }
}