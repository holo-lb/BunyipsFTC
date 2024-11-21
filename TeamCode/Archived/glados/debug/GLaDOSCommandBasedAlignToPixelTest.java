package org.murraybridgebunyips.glados.debug;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.murraybridgebunyips.bunyipslib.CommandBasedBunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.Controls;
import org.murraybridgebunyips.bunyipslib.drive.DualDeadwheelMecanumDrive;
import org.murraybridgebunyips.bunyipslib.drive.MecanumDrive;
import org.murraybridgebunyips.bunyipslib.external.pid.PIDController;
import org.murraybridgebunyips.bunyipslib.tasks.AlignToContourTask;
import org.murraybridgebunyips.bunyipslib.tasks.HolonomicDriveTask;
import org.murraybridgebunyips.bunyipslib.tasks.MoveToContourTask;
import org.murraybridgebunyips.bunyipslib.vision.Vision;
import org.murraybridgebunyips.bunyipslib.vision.processors.MultiColourThreshold;
import org.murraybridgebunyips.bunyipslib.vision.processors.centerstage.Pixels;
import org.murraybridgebunyips.glados.components.GLaDOSConfigCore;

/**
 * Align/move to a pixel using the command based system.
 *
 * @author Lucas Bubner, 2024
 */
@TeleOp(name = "Align To Pixel (Command Based)")
@Disabled
public class GLaDOSCommandBasedAlignToPixelTest extends CommandBasedBunyipsOpMode {
    private final GLaDOSConfigCore config = new GLaDOSConfigCore();
    private MecanumDrive drive;
    private Vision vision;
    private MultiColourThreshold pixels;

    @Override
    protected void onInitialise() {
        config.init();
        drive = new DualDeadwheelMecanumDrive(
                config.driveConstants, config.mecanumCoefficients,
                config.imu, config.frontLeft, config.frontRight,
                config.backLeft, config.backRight, config.localizerCoefficients,
                config.parallelDeadwheel, config.perpendicularDeadwheel
        );
        vision = new Vision(config.webcam);
        pixels = new MultiColourThreshold(Pixels.createProcessors());
        vision.init(pixels);
        vision.start(pixels);
        vision.startPreview();
    }

    @Override
    protected void assignCommands() {
        drive.setDefaultTask(new HolonomicDriveTask(gamepad1, drive, () -> false));
//        scheduler().whenHeld(Controller.User.ONE, Controller.Y)
//                .run(new RunTask(() -> drive.resetYaw()))
//                
        driver().whenPressed(Controls.LEFT_BUMPER)
                .run(new AlignToContourTask(gamepad1, drive, pixels, new PIDController(0.67, 0.25, 0.0)))
                .finishingIf(() -> !gamepad1.left_bumper);
        driver().whenPressed(Controls.RIGHT_BUMPER)
                .run(new MoveToContourTask(gamepad1, drive, pixels, new PIDController(0.36, 0.6, 0.0), new PIDController(0.67, 0.25, 0.0)))
                .finishingIf(() -> !gamepad1.right_bumper);
    }
}

