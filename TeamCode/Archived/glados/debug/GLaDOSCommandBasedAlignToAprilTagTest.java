package org.murraybridgebunyips.glados.debug;

import static org.murraybridgebunyips.bunyipslib.external.units.Units.Inches;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.murraybridgebunyips.bunyipslib.CommandBasedBunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.Controls;
import org.murraybridgebunyips.bunyipslib.drive.DualDeadwheelMecanumDrive;
import org.murraybridgebunyips.bunyipslib.drive.MecanumDrive;
import org.murraybridgebunyips.bunyipslib.tasks.HolonomicDriveTask;
import org.murraybridgebunyips.bunyipslib.tasks.MoveToAprilTagTask;
import org.murraybridgebunyips.bunyipslib.vision.Vision;
import org.murraybridgebunyips.bunyipslib.vision.processors.AprilTag;
import org.murraybridgebunyips.glados.components.GLaDOSConfigCore;

/**
 * Align to AprilTag.
 *
 * @author Lucas Bubner, 2024
 */
@TeleOp(name = "Align To AprilTag (Command Based)")
@Disabled
public class GLaDOSCommandBasedAlignToAprilTagTest extends CommandBasedBunyipsOpMode {
    private final GLaDOSConfigCore config = new GLaDOSConfigCore();
    private MecanumDrive drive;
    private Vision vision;
    private AprilTag aprilTag;

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
        aprilTag = new AprilTag();
        vision.init(aprilTag);
        vision.start(aprilTag);
        vision.startPreview();
    }

    @Override
    protected void assignCommands() {
        drive.setDefaultTask(new HolonomicDriveTask(gamepad1, drive, () -> false));
        driver().whenPressed(Controls.LEFT_BUMPER)
                .run(new MoveToAprilTagTask(gamepad1, drive, aprilTag, 2).withDesiredDistance(Inches.of(10)))
                .finishingIf(() -> !gamepad1.left_bumper);
    }
}

