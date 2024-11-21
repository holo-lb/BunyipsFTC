package org.murraybridgebunyips.wheatley.teleop;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.murraybridgebunyips.bunyipslib.CommandBasedBunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.Controller;
import org.murraybridgebunyips.bunyipslib.Controls;
import org.murraybridgebunyips.bunyipslib.drive.MecanumDrive;
import org.murraybridgebunyips.bunyipslib.subsystems.BlinkinLights;
import org.murraybridgebunyips.bunyipslib.subsystems.Cannon;
import org.murraybridgebunyips.bunyipslib.subsystems.DualServos;
import org.murraybridgebunyips.bunyipslib.subsystems.HoldableActuator;
import org.murraybridgebunyips.bunyipslib.tasks.HolonomicDriveTask;
import org.murraybridgebunyips.common.centerstage.tasks.PickUpPixelTask;
import org.murraybridgebunyips.wheatley.components.WheatleyConfig;

/**
 * Primary TeleOp for all of Wheatley's functions.
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
 * dpad_down: retract hook to absolute bottom<br>
 *
 * @author Lachlan Paul, 2024
 * @author Lucas Bubner, 2024
 */

@TeleOp(name = "TeleOp")
public class WheatleyTeleOp extends CommandBasedBunyipsOpMode {
    private final WheatleyConfig config = new WheatleyConfig();
    private MecanumDrive drive;
    private Cannon cannon;
    private HoldableActuator linearActuator;
    private HoldableActuator rotator;
    private DualServos claws;
    //    private Vision vision;
//    private MultiColourThreshold pixels;
    private BlinkinLights lights;

    @Override
    protected void onInitialise() {
        config.init();
        drive = new MecanumDrive(
                config.driveConstants, config.mecanumCoefficients,
                config.imu, config.fl, config.fr, config.bl, config.br
        );
        cannon = new Cannon(config.launcher);
        linearActuator = new HoldableActuator(config.linearActuator)
                .withBottomSwitch(config.bottomLimit);
//                .withTopSwitch(config.topLimit);
        rotator = new HoldableActuator(config.clawRotator)
//                .withAngleLimits(Degrees.zero(), Degrees.of(180))
                .withPowerClamps(-0.33, 0.33)
                .withName("Claw Rotator");
        claws = new DualServos(config.leftPixel, config.rightPixel, 1.0, 0.0, 0.0, 1.0);

//        vision = new Vision(config.webcam);
//        pixels = new MultiColourThreshold(Pixels.createProcessors());
//        vision.init(pixels);
//        vision.start(pixels);
//        vision.startPreview();

        lights = new BlinkinLights(config.lights, RevBlinkinLedDriver.BlinkinPattern.RAINBOW_FOREST_PALETTE);

        gamepad1.set(Controls.AnalogGroup.STICKS, Controller.SQUARE);
    }

    @Override
    protected void assignCommands() {
        driver().when(Controls.Analog.RIGHT_TRIGGER, (v) -> v == 1.0)
                .run(cannon.tasks.fire());
        operator().whenPressed(Controls.BACK)
                .run(cannon.tasks.reset());

        operator().whenPressed(Controls.X)
                .run(claws.tasks.toggleLeft());
        operator().whenPressed(Controls.B)
                .run(claws.tasks.toggleRight());

        operator().whenPressed(Controls.DPAD_UP)
//                .run(rotator.gotoTimeTask(Degrees.of(60), Seconds.of(2)));
                .run(rotator.tasks.goTo(100));
        operator().whenPressed(Controls.DPAD_DOWN)
                .run(rotator.tasks.home());

//        driver().whenPressed(Controls.RIGHT_BUMPER)
//                .run(new AlignToContourTask<>(gamepad1, drive, pixels, new PIDController(0.67, 0.25, 0.0)))
//                .finishingIf(() -> !gamepad1.right_bumper);

        operator().whenPressed(Controls.A)
                .run(linearActuator.tasks.home())
                .finishingIf(() -> gamepad2.lsy != 0.0f);

        operator().when(Controls.Analog.RIGHT_TRIGGER, (v) -> v == 1.0)
                .run(new PickUpPixelTask(linearActuator, claws));

        driver().whenPressed(Controls.Y)
                .run(() -> config.imu.resetYaw());

        linearActuator.setDefaultTask(linearActuator.tasks.control(() -> -gamepad2.lsy));
        rotator.setDefaultTask(rotator.tasks.control(() -> gamepad2.rsy));
        drive.setDefaultTask(new HolonomicDriveTask(gamepad1, drive, () -> false));
    }

    @Override
    protected void periodic() {
        // LED Management
        if (config.bottomLimit.isPressed()) { // Make an && for the top limit when it's reinstated
            lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.RED);
        }

        // Some drivers have noted that they sometimes cannot tell whether a claw is open or closed.
        // Hopefully this helps. Update: It did :)
        telemetry.add("\n---------");

        // The actual string is set to the opposite of what you might expect, by driver request.
        telemetry.add("Left Claw: " + (claws.isOpen(DualServos.ServoSide.LEFT) ? "Closed" : "Open")).big();
        telemetry.add("Right Claw: " + (claws.isOpen(DualServos.ServoSide.RIGHT) ? "Closed" : "Open")).big();
        telemetry.add("---------\n");

//        telemetry.add("Top Switch Is %", config.topLimit.isPressed() ? "Pressed" : "Not Pressed");
        telemetry.add("Bottom Switch Is %", config.bottomLimit.isPressed() ? "Pressed" : "Not Pressed");
    }
}