package org.murraybridgebunyips.glados.teleop;

import static org.murraybridgebunyips.bunyipslib.external.units.Units.Amps;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Seconds;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.murraybridgebunyips.bunyipslib.CommandBasedBunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.Controller;
import org.murraybridgebunyips.bunyipslib.Controls;
import org.murraybridgebunyips.bunyipslib.drive.DualDeadwheelMecanumDrive;
import org.murraybridgebunyips.bunyipslib.drive.MecanumDrive;
import org.murraybridgebunyips.bunyipslib.external.pid.PIDController;
import org.murraybridgebunyips.bunyipslib.subsystems.Cannon;
import org.murraybridgebunyips.bunyipslib.subsystems.DualServos;
import org.murraybridgebunyips.bunyipslib.subsystems.HoldableActuator;
import org.murraybridgebunyips.bunyipslib.tasks.AlignToContourTask;
import org.murraybridgebunyips.bunyipslib.tasks.HolonomicDriveTask;
import org.murraybridgebunyips.bunyipslib.vision.Vision;
import org.murraybridgebunyips.bunyipslib.vision.processors.MultiColourThreshold;
import org.murraybridgebunyips.bunyipslib.vision.processors.centerstage.Pixels;
import org.murraybridgebunyips.glados.components.GLaDOSConfigCore;

/**
 * TeleOp for GLaDOS robot FTC 15215
 *
 * @author Lucas Bubner, 2024
 * @author Lachlan Paul, 2024
 */
@Config
@TeleOp(name = "TeleOp")
public class GLaDOSTeleOp extends CommandBasedBunyipsOpMode {
    protected final GLaDOSConfigCore config = new GLaDOSConfigCore();
    protected MecanumDrive drive;
    protected HoldableActuator arm;
    protected DualServos claws;
    protected Cannon cannon;
    protected HoldableActuator suspender;
    protected Vision vision;
    protected MultiColourThreshold pixels;

    @Override
    protected void onInitialise() {
        config.init();
        drive = new DualDeadwheelMecanumDrive(
                config.driveConstants, config.mecanumCoefficients,
                config.imu, config.frontLeft, config.frontRight,
                config.backLeft, config.backRight, config.localizerCoefficients,
                config.parallelDeadwheel, config.perpendicularDeadwheel
        ).withName("Drive");
        vision = new Vision(config.webcam)
                .withName("Forward Camera");
        arm = new HoldableActuator(config.arm)
                .withPowerClamps(-0.3, 0.3)
                .withHomingOvercurrent(Amps.of(1), Seconds.of(0.5))
                .withName("Pixel Arm");
        cannon = new Cannon(config.launcher);
        suspender = new HoldableActuator(config.suspenderActuator)
                .withBottomSwitch(config.bottomLimit)
                .withName("Suspender");
        claws = new DualServos(config.leftPixel, config.rightPixel, 1.0, 0.0, 0.0, 1.0)
                .withName("Pixel Claws");
        /*giulio*/

        telemetry.add("Robot is assumed to be facing angle: % deg", Math.toDegrees(drive.getPoseEstimate().getHeading()));

//        RampingSupplier armRamping = new RampingSupplier(() -> gamepad2.lsy);
//        gamepad2.set(Controls.Analog.LEFT_STICK_Y, armRamping::get);
        gamepad1.set(Controls.AnalogGroup.STICKS, Controller.SQUARE);
        pixels = new MultiColourThreshold(Pixels.createProcessors());

        configureVision();
    }

    protected void configureVision() {
        vision.init(pixels);
        vision.start(pixels);
    }

    @Override
    protected void assignCommands() {
//        drive.setDefaultTask(new HolonomicVectorDriveTask(gamepad1, drive, () -> false)
//                .withTranslationalPID(0.1, 0, 0)
//                .withRotationalPID(1, 0, 0.0001));
        drive.setDefaultTask(new HolonomicDriveTask(gamepad1, drive, () -> false));

        driver().whenPressed(Controls.RIGHT_BUMPER)
                .run(new AlignToContourTask(() -> gamepad2.lsx, () -> gamepad1.lsy, () -> gamepad1.rsx, drive, pixels, new PIDController(0.67, 0.25, 0)))
                .finishingIf(() -> !gamepad1.rb);
        driver().when(Controls.Analog.RIGHT_TRIGGER, (v) -> v == 1.0)
                .run(cannon.tasks.fire());
        driver().whenPressed(Controls.BACK)
                /*print("Hello, World!")
                        class MY=yclass             - lachlan paul*/
                .run(cannon.tasks.fire());

        suspender.setDefaultTask(suspender.tasks.control(() -> -gamepad2.rsy));

        operator().whenPressed(Controls.B)
                .run(claws.tasks.toggleRight());
        operator().whenPressed(Controls.X)
                .run(claws.tasks.toggleLeft());

        operator().whenPressed(Controls.A)
                .run(suspender.tasks.home())
                .finishingIf(() -> gamepad2.rsy != 0.0f);
        arm.setDefaultTask(arm.tasks.control(() -> gamepad2.lsy));
    }
}
