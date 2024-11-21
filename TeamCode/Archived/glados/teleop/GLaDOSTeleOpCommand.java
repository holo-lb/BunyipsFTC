package org.murraybridgebunyips.glados.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.murraybridgebunyips.bunyipslib.Cannon;
import org.murraybridgebunyips.bunyipslib.CommandBasedBunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.Controller;
import org.murraybridgebunyips.bunyipslib.DualServos;
import org.murraybridgebunyips.bunyipslib.InputMultiplier;
import org.murraybridgebunyips.bunyipslib.drive.DualDeadwheelMecanumDrive;
import org.murraybridgebunyips.bunyipslib.drive.MecanumDrive;
import org.murraybridgebunyips.bunyipslib.tasks.GetRedTeamPropTask;
import org.murraybridgebunyips.bunyipslib.tasks.HolonomicDriveTask;
import org.murraybridgebunyips.bunyipslib.vision.Vision;
import org.murraybridgebunyips.common.personalitycore.PersonalityCoreClawRotator;
import org.murraybridgebunyips.common.personalitycore.PersonalityCoreForwardServo;
import org.murraybridgebunyips.common.personalitycore.PersonalityCoreHook;
import org.murraybridgebunyips.common.personalitycore.PersonalityCoreLinearActuator;
import org.murraybridgebunyips.bunyipslib.vision.processors.centerstage.RedTeamProp;
import org.murraybridgebunyips.glados.components.GLaDOSConfigCore;

/**
 * TeleOp for GLaDOS robot FTC 15215, command-based
 * gamepad1:
 * left_stick_x: strafe
 * left_stick_y: forward/backward
 * right_stick_x: turn
 * right_trigger: fire cannon
 * options: reset cannon
 * gamepad2:
 * x: toggle left claw
 * b: toggle right claw
 * y: align claw to board
 * a: align claw to ground
 * left_stick_y: actuate the management rail
 * right_stick_y: move claw mover
 * dpad_up: extend hook one position
 * dpad_down: retract hook one position
 *
 * @author Lucas Bubner, 2024
 */
@TeleOp(name = "TeleOp (Command Based)")
public class GLaDOSTeleOpCommand extends CommandBasedBunyipsOpMode {
    private final GLaDOSConfigCore config = new GLaDOSConfigCore();
    private InputMultiplier speed;
    private MecanumDrive drive;
    private PersonalityCoreClawRotator clawRotator;
    private PersonalityCoreForwardServo pixelMotion;
    private PersonalityCoreHook hook;
    private PersonalityCoreLinearActuator linearActuator;
    private DualServos claws;
    private Vision vision;
    private GetRedTeamPropTask get;
    private Cannon cannon;
    private RedTeamProp p;

    @Override
    protected void onInitialisation() {
        config.init();
        drive = new DualDeadwheelMecanumDrive(
                config.driveConstants, config.mecanumCoefficients,
                hardwareMap.voltageSensor, config.imu, config.frontLeft, config.frontRight,
                config.backLeft, config.backRight, config.localizerCoefficients,
                config.parallelDeadwheel, config.perpendicularDeadwheel
        );
        speed = new InputMultiplier(0.25, 0.5, 1.0).withDefaultIndex(1).withName("Drive");
        cannon = new Cannon(config.launcher);
        clawRotator = new PersonalityCoreClawRotator(config.pixelAlignment);
        pixelMotion = new PersonalityCoreForwardServo(config.pixelMotion);
        hook = new PersonalityCoreHook(config.suspenderHook);
        linearActuator = new PersonalityCoreLinearActuator(config.suspenderActuator);
        vision = new Vision(config.webcam);
        claws = new DualServos(config.leftPixel, config.rightPixel, 0.0, 1.0, 1.0, 0.0);
        p = new RedTeamProp();
        get = new GetRedTeamPropTask(p);
        vision.init(vision.raw, p);
        vision.start(vision.raw, p);
        vision.startPreview();
        vision.setPreview(p);
        addSubsystems(drive, clawRotator, pixelMotion, hook, linearActuator, claws, cannon, speed);
    }

    @Override
    protected void assignCommands() {
        scheduler().always()
                .run(get);

        scheduler().always()
                .run(() -> addTelemetry(get.getPosition()))
                .muted();

        scheduler().whenPressed(Controller.User.TWO, Controller.X)
                .run(claws.toggleServoTask(DualServos.ServoSide.LEFT));
        scheduler().whenPressed(Controller.User.TWO, Controller.B)
                .run(claws.toggleServoTask(DualServos.ServoSide.RIGHT));

        scheduler().whenPressed(Controller.User.TWO, Controller.Y)
                .run(clawRotator.faceBoardTask());
        scheduler().whenPressed(Controller.User.TWO, Controller.A)
                .run(clawRotator.faceGroundTask());

        pixelMotion.setDefaultTask(pixelMotion.dpadControlTask(() -> gamepad2.dpad_up, () -> gamepad2.dpad_down));
        linearActuator.setDefaultTask(linearActuator.joystickControlTask(() -> gamepad2.left_stick_y));
        clawRotator.setDefaultTask(clawRotator.joystickControlTask(() -> gamepad2.right_stick_y));

        scheduler().whenPressed(Controller.User.ONE, Controller.RIGHT_BUMPER)
                .run(speed.incrementTask());
        scheduler().whenPressed(Controller.User.ONE, Controller.LEFT_BUMPER)
                .run(speed.decrementTask());

        scheduler().when(() -> gamepad1.right_trigger == 1.0)
                .run(cannon.tasks.fire());
        scheduler().whenPressed(Controller.User.ONE, Controller.BACK)
                .run(cannon.tasks.reset());

        scheduler().whenPressed(Controller.User.ONE, Controller.DPAD_UP)
                .run(hook.extendTask());
        scheduler().whenPressed(Controller.User.ONE, Controller.DPAD_DOWN)
                .run(hook.retractTask());

        drive.setDefaultTask(new HolonomicDriveTask<>(gamepad1, drive, speed::getMultiplier, () -> false));
    }
}
